import { Component, ElementRef, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { AlertService } from 'app/core/util/alert.service';
import { UserService } from 'app/entities/user/user.service';
import * as Papa from 'papaparse';
import { LedgerImportEntry, CostCenterImport } from '../ledger-import-entry.model';
import { LedgerService } from '../ledger.service';

type ImportState = 'initial' | 'opening' | 'opened' | 'importing' | 'imported';

@Component({
  selector: 'jhi-ledger-import',
  templateUrl: './ledger-import.component.html',
  styleUrls: ['./ledger-import.component.scss'],
})
export class LedgerImportComponent {
  @ViewChild('file') fileInput: ElementRef<HTMLInputElement> | undefined = undefined;
  entries: LedgerImportEntry[] = [];
  fileName?: string;
  state: ImportState = 'initial';

  constructor(
    protected sanitizer: DomSanitizer,
    protected alertService: AlertService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected ledgerService: LedgerService
  ) {}

  trackId(index: number, entry: LedgerImportEntry): string {
    return entry.no;
  }

  async open(): Promise<void> {
    this.state = 'opening';
    try {
      const input = this.fileInput!.nativeElement;

      // Check if the user
      if (input.files === null || input.files.length === 0) {
        this.reset();
        return;
      }

      const file = input.files[0];
      const result = await new Promise((resolve: (value: Papa.ParseResult<Record<string, string>>) => void, reject) => {
        Papa.parse(file, {
          worker: false,
          header: true,
          newline: '\r\n',
          complete(parseResult: Papa.ParseResult<Record<string, string>>): void {
            resolve(parseResult);
          },
          error(error) {
            reject(new Error(error.message));
          },
        });
      });

      const entries = this.parseEntries(result.data);

      this.fileName = file.name;
      this.entries = entries;
      this.state = 'opened';

      this.alertService.clear();
      for (const error of result.errors) {
        const alertMessage = `${error.type} | ${error.code} | Row: ${error.row} | ${error.message}`;
        this.alertService.addAlert({ type: 'warning', message: alertMessage, timeout: -1 });
      }
    } catch (e) {
      this.reset();
      this.alertService.addAlert({ type: 'danger', translationKey: 'error.unknown', timeout: -1 });
      throw e;
    }
  }

  async import(): Promise<void> {
    this.state = 'importing';
    try {
      const entries = this.entries.slice();
      await this.ledgerService.import(entries).toPromise();
    } finally {
      this.state = 'opened';
    }
    this.alertService.clear();
    this.alertService.addAlert({ type: 'success', message: 'Die Datei wurde erfolgreich importiert.', timeout: -1 });
  }

  reset(): void {
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
    this.fileName = undefined;
    this.entries = [];
    this.state = 'initial';
  }

  get canOpen(): boolean {
    return this.state === 'initial' || this.state === 'opened';
  }

  get canRefresh(): boolean {
    return this.state === 'opened';
  }

  get canImport(): boolean {
    return this.state === 'opened' && this.entries.length > 0;
  }

  get canShowTable(): boolean {
    return this.state !== 'initial';
  }

  get isImporting(): boolean {
    return this.state === 'importing';
  }

  private parseEntries(data: Record<string, string>[]): LedgerImportEntry[] {
    const entries: LedgerImportEntry[] = [];
    const noSet = new Set<string>();
    const costCenterTable = parseCostCenters(data);

    for (const record of data) {
      // Skip sum rows.
      if (!Object.prototype.hasOwnProperty.call(record, 'join_nr') || record['join_nr'] === '') {
        continue;
      }
      const meta: Record<string, any> = JSON.parse(record['join_nr']);
      const no: string = meta['join_nr'];

      // Due to a buggy CSV export, join no are duplicated. If the row
      // has a join no, then it is a sum row and therefore skipped.
      if (noSet.has(no)) {
        continue;
      }

      const costCenter1 = costCenterTable.get(record['ins']);
      const costCenter2 = costCenterTable.get(record['ins2']);
      const costCenter3 = costCenterTable.get(record['ins3']);

      // Make sure that all cost centers are valid.
      // TODO: Better error handling.
      if (costCenter1 === undefined || costCenter2 === undefined || costCenter3 === undefined) {
        continue;
      }

      const entry = new LedgerImportEntry(
        record['Jahr'],
        costCenter1,
        costCenter2,
        costCenter3,
        no,
        meta['name'],
        coalesce(meta['a_nr']),
        meta['b_dat'],
        record['Einnahmen'],
        record['Ausgaben'],
        record['Verpflichtungen'],
        coalesce(meta['fb']),
        coalesce(meta['fb_dname']),
        coalesce(meta['koa_nr']),
        coalesce(meta['koa_dname'])
      );

      noSet.add(no);
      entries.push(entry);
    }
    return entries;
  }
}

function coalesce(value: string): string | null {
  return value === '' ? null : value;
}

function parseCostCenters(data: Record<string, string>[]): Map<string, CostCenterImport> {
  const costCenterRegex = /^([0-9]+) \((.+)\)$/;
  const costCenters = new Map<string, CostCenterImport>();
  for (const record of data) {
    for (const costCenterStr of [record['ins'], record['ins2'], record['ins3']]) {
      if (!costCenters.has(costCenterStr)) {
        const matches = costCenterRegex.exec(costCenterStr);
        if (matches !== null) {
          costCenters.set(costCenterStr, new CostCenterImport(matches[1], matches[2]));
        }
      }
    }
  }
  return costCenters;
}
