import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { PipesModule } from 'app/pipes/pipes.module';
import { CommentEntryComponent } from 'app/shared/comment/comment-entry.component';
import { CommentFormComponent } from 'app/shared/comment/comment-form.component';
import { SharedModule } from 'app/shared/shared.module';

import { LedgerEntryComponent } from './ledger-entry.component';
import { entryRoute } from './ledger-entry.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([entryRoute]), PipesModule],
  declarations: [LedgerEntryComponent, CommentEntryComponent, CommentFormComponent],
})
export class LedgerEntryModule {}
