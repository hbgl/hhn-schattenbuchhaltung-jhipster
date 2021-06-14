import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITagCustomType, TagCustomType } from '../tag-custom-type.model';
import { TagCustomTypeService } from '../service/tag-custom-type.service';

@Injectable({ providedIn: 'root' })
export class TagCustomTypeRoutingResolveService implements Resolve<ITagCustomType> {
  constructor(protected service: TagCustomTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITagCustomType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tagCustomType: HttpResponse<TagCustomType>) => {
          if (tagCustomType.body) {
            return of(tagCustomType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TagCustomType());
  }
}
