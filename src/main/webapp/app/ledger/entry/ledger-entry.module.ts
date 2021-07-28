import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { PipesModule } from 'app/pipes/pipes.module';
import { SharedModule } from 'app/shared/shared.module';
import { LedgerEntryCommentComponent } from './ledger-entry-comment.component';

import { LedgerEntryComponent } from './ledger-entry.component';
import { entryRoute } from './ledger-entry.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([entryRoute]), PipesModule],
  declarations: [LedgerEntryComponent, LedgerEntryCommentComponent],
})
export class LedgerEntryModule {}
