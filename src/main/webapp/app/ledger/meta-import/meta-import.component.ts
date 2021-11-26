import { Component, ElementRef, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { AlertService } from 'app/core/util/alert.service';
import { UserService } from 'app/entities/user/user.service';
import { plainToClass, Type } from 'class-transformer';
import { LedgerService } from '../ledger.service';

enum ImportState {
  Initial,
  Opening,
  Opened,
  Importing,
  Imported,
}

class MetaImport {
  @Type(() => Date)
  public instant: Date;

  constructor(public version: number, public userId: string, public userEmail: string, public userName: string, instant: Date) {
    this.instant = instant;
  }
}

@Component({
  selector: 'jhi-ledger-meta-import',
  templateUrl: './meta-import.component.html',
})
export class LedgerMetaImportComponent {
  @ViewChild('file') fileInput?: ElementRef<HTMLInputElement>;
  data?: MetaImport;
  dataJson?: string;
  fileName?: string;
  state = ImportState.Initial;

  constructor(
    protected sanitizer: DomSanitizer,
    protected alertService: AlertService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected ledgerService: LedgerService
  ) {}

  async open(): Promise<void> {
    this.state = ImportState.Opening;
    try {
      const input = this.fileInput!.nativeElement;

      // Check if the user
      if (input.files === null || input.files.length === 0) {
        this.reset();
        return;
      }

      const file = input.files[0];

      const json: string = await new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = reject;
        reader.readAsText(file);
      });

      const plain = JSON.parse(json) as Record<string, unknown>;
      this.data = plainToClass(MetaImport, plain);
      this.dataJson = json;
      this.fileName = file.name;
      this.state = ImportState.Opened;

      this.alertService.clear();
    } catch (e) {
      this.reset();
      this.alertService.addAlert({ type: 'danger', translationKey: 'error.unknown', timeout: -1 });
      throw e;
    }
  }

  async import(): Promise<void> {
    this.state = ImportState.Importing;
    try {
      await this.ledgerService.importMeta(this.dataJson!).toPromise();
    } finally {
      this.state = ImportState.Opened;
    }
    this.alertService.clear();
    this.alertService.addAlert({ type: 'success', message: 'Die Datei wurde erfolgreich importiert.', timeout: -1 });
  }

  reset(): void {
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
    this.fileName = undefined;
    this.data = undefined;
    this.state = ImportState.Initial;
  }

  get canOpen(): boolean {
    return this.state === ImportState.Initial || this.state === ImportState.Opened;
  }

  get canRefresh(): boolean {
    return this.state === ImportState.Opened;
  }

  get canImport(): boolean {
    return this.state === ImportState.Opened;
  }

  get canShowInfo(): boolean {
    return this.state !== ImportState.Initial;
  }

  get isImporting(): boolean {
    return this.state === ImportState.Importing;
  }
}
