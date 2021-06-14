import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { TagCustomTypeComponent } from './list/tag-custom-type.component';
import { TagCustomTypeDetailComponent } from './detail/tag-custom-type-detail.component';
import { TagCustomTypeUpdateComponent } from './update/tag-custom-type-update.component';
import { TagCustomTypeDeleteDialogComponent } from './delete/tag-custom-type-delete-dialog.component';
import { TagCustomTypeRoutingModule } from './route/tag-custom-type-routing.module';

@NgModule({
  imports: [SharedModule, TagCustomTypeRoutingModule],
  declarations: [TagCustomTypeComponent, TagCustomTypeDetailComponent, TagCustomTypeUpdateComponent, TagCustomTypeDeleteDialogComponent],
  entryComponents: [TagCustomTypeDeleteDialogComponent],
})
export class TagCustomTypeModule {}
