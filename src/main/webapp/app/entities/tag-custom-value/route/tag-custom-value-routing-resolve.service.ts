import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITagCustomValue, TagCustomValue } from '../tag-custom-value.model';
import { TagCustomValueService } from '../service/tag-custom-value.service';

@Injectable({ providedIn: 'root' })
export class TagCustomValueRoutingResolveService implements Resolve<ITagCustomValue> {
  constructor(protected service: TagCustomValueService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITagCustomValue> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tagCustomValue: HttpResponse<TagCustomValue>) => {
          if (tagCustomValue.body) {
            return of(tagCustomValue.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TagCustomValue());
  }
}
