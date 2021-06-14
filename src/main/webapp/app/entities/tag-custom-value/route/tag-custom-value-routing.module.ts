import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TagCustomValueComponent } from '../list/tag-custom-value.component';
import { TagCustomValueDetailComponent } from '../detail/tag-custom-value-detail.component';
import { TagCustomValueUpdateComponent } from '../update/tag-custom-value-update.component';
import { TagCustomValueRoutingResolveService } from './tag-custom-value-routing-resolve.service';

const tagCustomValueRoute: Routes = [
  {
    path: '',
    component: TagCustomValueComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TagCustomValueDetailComponent,
    resolve: {
      tagCustomValue: TagCustomValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TagCustomValueUpdateComponent,
    resolve: {
      tagCustomValue: TagCustomValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TagCustomValueUpdateComponent,
    resolve: {
      tagCustomValue: TagCustomValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tagCustomValueRoute)],
  exports: [RouterModule],
})
export class TagCustomValueRoutingModule {}
