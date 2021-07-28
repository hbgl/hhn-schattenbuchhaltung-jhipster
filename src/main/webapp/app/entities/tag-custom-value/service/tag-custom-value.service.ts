import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITagCustomValue, getTagCustomValueIdentifier } from '../tag-custom-value.model';

export type EntityResponseType = HttpResponse<ITagCustomValue>;
export type EntityArrayResponseType = HttpResponse<ITagCustomValue[]>;

@Injectable({ providedIn: 'root' })
export class TagCustomValueService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/tag-custom-values');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(tagCustomValue: ITagCustomValue): Observable<EntityResponseType> {
    return this.http.post<ITagCustomValue>(this.resourceUrl, tagCustomValue, { observe: 'response' });
  }

  update(tagCustomValue: ITagCustomValue): Observable<EntityResponseType> {
    return this.http.put<ITagCustomValue>(`${this.resourceUrl}/${getTagCustomValueIdentifier(tagCustomValue) as number}`, tagCustomValue, {
      observe: 'response',
    });
  }

  partialUpdate(tagCustomValue: ITagCustomValue): Observable<EntityResponseType> {
    return this.http.patch<ITagCustomValue>(
      `${this.resourceUrl}/${getTagCustomValueIdentifier(tagCustomValue) as number}`,
      tagCustomValue,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITagCustomValue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITagCustomValue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTagCustomValueToCollectionIfMissing(
    tagCustomValueCollection: ITagCustomValue[],
    ...tagCustomValuesToCheck: (ITagCustomValue | null | undefined)[]
  ): ITagCustomValue[] {
    const tagCustomValues: ITagCustomValue[] = tagCustomValuesToCheck.filter(isPresent);
    if (tagCustomValues.length > 0) {
      const tagCustomValueCollectionIdentifiers = tagCustomValueCollection.map(
        tagCustomValueItem => getTagCustomValueIdentifier(tagCustomValueItem)!
      );
      const tagCustomValuesToAdd = tagCustomValues.filter(tagCustomValueItem => {
        const tagCustomValueIdentifier = getTagCustomValueIdentifier(tagCustomValueItem);
        if (tagCustomValueIdentifier == null || tagCustomValueCollectionIdentifiers.includes(tagCustomValueIdentifier)) {
          return false;
        }
        tagCustomValueCollectionIdentifiers.push(tagCustomValueIdentifier);
        return true;
      });
      return [...tagCustomValuesToAdd, ...tagCustomValueCollection];
    }
    return tagCustomValueCollection;
  }
}
