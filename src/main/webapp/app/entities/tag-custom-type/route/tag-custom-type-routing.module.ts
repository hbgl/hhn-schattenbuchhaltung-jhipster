import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TagCustomTypeComponent } from '../list/tag-custom-type.component';
import { TagCustomTypeDetailComponent } from '../detail/tag-custom-type-detail.component';
import { TagCustomTypeUpdateComponent } from '../update/tag-custom-type-update.component';
import { TagCustomTypeRoutingResolveService } from './tag-custom-type-routing-resolve.service';

const tagCustomTypeRoute: Routes = [
  {
    path: '',
    component: TagCustomTypeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TagCustomTypeDetailComponent,
    resolve: {
      tagCustomType: TagCustomTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TagCustomTypeUpdateComponent,
    resolve: {
      tagCustomType: TagCustomTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TagCustomTypeUpdateComponent,
    resolve: {
      tagCustomType: TagCustomTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tagCustomTypeRoute)],
  exports: [RouterModule],
})
export class TagCustomTypeRoutingModule {}
