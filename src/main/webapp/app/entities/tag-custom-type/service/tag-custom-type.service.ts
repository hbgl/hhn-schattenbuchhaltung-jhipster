import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITagCustomType, getTagCustomTypeIdentifier } from '../tag-custom-type.model';

export type EntityResponseType = HttpResponse<ITagCustomType>;
export type EntityArrayResponseType = HttpResponse<ITagCustomType[]>;

@Injectable({ providedIn: 'root' })
export class TagCustomTypeService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/tag-custom-types');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/tag-custom-types');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(tagCustomType: ITagCustomType): Observable<EntityResponseType> {
    return this.http.post<ITagCustomType>(this.resourceUrl, tagCustomType, { observe: 'response' });
  }

  update(tagCustomType: ITagCustomType): Observable<EntityResponseType> {
    return this.http.put<ITagCustomType>(`${this.resourceUrl}/${getTagCustomTypeIdentifier(tagCustomType) as number}`, tagCustomType, {
      observe: 'response',
    });
  }

  partialUpdate(tagCustomType: ITagCustomType): Observable<EntityResponseType> {
    return this.http.patch<ITagCustomType>(`${this.resourceUrl}/${getTagCustomTypeIdentifier(tagCustomType) as number}`, tagCustomType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITagCustomType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITagCustomType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITagCustomType[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addTagCustomTypeToCollectionIfMissing(
    tagCustomTypeCollection: ITagCustomType[],
    ...tagCustomTypesToCheck: (ITagCustomType | null | undefined)[]
  ): ITagCustomType[] {
    const tagCustomTypes: ITagCustomType[] = tagCustomTypesToCheck.filter(isPresent);
    if (tagCustomTypes.length > 0) {
      const tagCustomTypeCollectionIdentifiers = tagCustomTypeCollection.map(
        tagCustomTypeItem => getTagCustomTypeIdentifier(tagCustomTypeItem)!
      );
      const tagCustomTypesToAdd = tagCustomTypes.filter(tagCustomTypeItem => {
        const tagCustomTypeIdentifier = getTagCustomTypeIdentifier(tagCustomTypeItem);
        if (tagCustomTypeIdentifier == null || tagCustomTypeCollectionIdentifiers.includes(tagCustomTypeIdentifier)) {
          return false;
        }
        tagCustomTypeCollectionIdentifiers.push(tagCustomTypeIdentifier);
        return true;
      });
      return [...tagCustomTypesToAdd, ...tagCustomTypeCollection];
    }
    return tagCustomTypeCollection;
  }
}
