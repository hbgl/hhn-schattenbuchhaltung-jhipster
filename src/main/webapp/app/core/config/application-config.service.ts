import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ApplicationConfigService {
  private _endpointPrefix = '';
  private _indexedDbName = '';

  public set indexedDbName(value: string) {
    this._indexedDbName = value;
  }

  public get indexedDbName(): string {
    return this._indexedDbName;
  }

  public set endpointPrefix(value: string) {
    this._endpointPrefix = value;
  }

  getEndpointFor(api: string, microservice?: string): string {
    if (microservice) {
      return `${this._endpointPrefix}services/${microservice}/${api}`;
    }
    return `${this._endpointPrefix}${api}`;
  }
}
