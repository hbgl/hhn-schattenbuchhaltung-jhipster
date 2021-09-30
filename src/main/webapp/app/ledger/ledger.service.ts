import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { LedgerEntryDetail, LedgerListEntry, Tag } from './ledger-entry.model';
import { map } from 'rxjs/operators';
import { plainToClass } from 'class-transformer';
import { LedgerImportEntry } from './ledger-import-entry.model';

@Injectable({ providedIn: 'root' })
export class LedgerService {
  public baseUrl = this.applicationConfigService.getEndpointFor('api/ledger');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  list(): Observable<LedgerListEntry[]> {
    return this.http.get<unknown[]>(this.baseUrl).pipe(map(res => plainToClass(LedgerListEntry, res)));
  }

  detail(no: string): Observable<LedgerEntryDetail> {
    return this.http
      .get<unknown>(`${this.baseUrl}/entry/${encodeURIComponent(no)}`, { observe: 'response' })
      .pipe(map(res => plainToClass(LedgerEntryDetail, res.body)));
  }

  import(entries: LedgerImportEntry[]): Observable<HttpResponse<{}>> {
    return this.http.post<LedgerImportEntry[]>(`${this.baseUrl}/import`, entries, { observe: 'response' });
  }

  updateTags(no: string, assignTags: string[], deleteTags: string[]): Observable<Tag[]> {
    const input = {
      assignTags,
      deleteTags,
    };
    return this.http.put<unknown[]>(`${this.baseUrl}/entry/${encodeURIComponent(no)}/tags`, input).pipe(map(res => plainToClass(Tag, res)));
  }

  autocompleteTag(text: string, existingNormalizedTexts: string[]): Observable<Tag[]> {
    return this.http
      .get<unknown[]>(`${this.baseUrl}/tag/autocomplete`, {
        params: {
          text,
          existing: existingNormalizedTexts.join(','),
        },
      })
      .pipe(map(res => plainToClass(Tag, res)));
  }
}
