import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { LedgerEntry } from '../ledger-entry.model';
import { map } from 'rxjs/operators';
import { plainToClass } from 'class-transformer';

@Injectable({ providedIn: 'root' })
export class LedgerListService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/ledger');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  ledger(): Observable<LedgerEntry[]> {
    return this.http
      .get<unknown[]>(this.resourceUrl, { observe: 'response' })
      .pipe(map(res => (res.body === null ? [] : plainToClass(LedgerEntry, res.body))));
  }
}
