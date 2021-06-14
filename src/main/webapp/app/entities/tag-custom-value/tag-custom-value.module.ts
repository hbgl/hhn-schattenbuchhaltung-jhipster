import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { TagCustomValueComponent } from './list/tag-custom-value.component';
import { TagCustomValueDetailComponent } from './detail/tag-custom-value-detail.component';
import { TagCustomValueUpdateComponent } from './update/tag-custom-value-update.component';
import { TagCustomValueDeleteDialogComponent } from './delete/tag-custom-value-delete-dialog.component';
import { TagCustomValueRoutingModule } from './route/tag-custom-value-routing.module';

@NgModule({
  imports: [SharedModule, TagCustomValueRoutingModule],
  declarations: [
    TagCustomValueComponent,
    TagCustomValueDetailComponent,
    TagCustomValueUpdateComponent,
    TagCustomValueDeleteDialogComponent,
  ],
  entryComponents: [TagCustomValueDeleteDialogComponent],
})
export class TagCustomValueModule {}
