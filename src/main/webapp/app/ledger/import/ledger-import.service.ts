import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { LedgerImportEntry } from '../ledger-import-entry.model';

@Injectable({ providedIn: 'root' })
export class LedgerImportService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/ledger/import');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  import(entries: LedgerImportEntry[]): Observable<HttpResponse<{}>> {
    return this.http.post<LedgerImportEntry[]>(this.resourceUrl, entries, { observe: 'response' });
  }
}
