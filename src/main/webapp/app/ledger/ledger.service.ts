import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { LedgerEntryDetail, LedgerListEntry } from './ledger-entry.model';
import { map } from 'rxjs/operators';
import { plainToClass } from 'class-transformer';
import { LedgerImportEntry } from './ledger-import-entry.model';

@Injectable({ providedIn: 'root' })
export class LedgerService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/ledger');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  list(): Observable<LedgerListEntry[]> {
    return this.http
      .get<unknown[]>(this.resourceUrl, { observe: 'response' })
      .pipe(map(res => (res.body === null ? [] : plainToClass(LedgerListEntry, res.body))));
  }

  detail(no: string): Observable<LedgerEntryDetail> {
    return this.http
      .get<unknown>(`${this.resourceUrl}/entry/${encodeURIComponent(no)}`, { observe: 'response' })
      .pipe(map(res => plainToClass(LedgerEntryDetail, res.body)));
  }

  import(entries: LedgerImportEntry[]): Observable<HttpResponse<{}>> {
    return this.http.post<LedgerImportEntry[]>(`${this.resourceUrl}/import`, entries, { observe: 'response' });
  }
}
