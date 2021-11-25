import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';

import { LedgerUpdateComponent } from './ledger-update.component';
import { importRoute } from './ledger-update.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([importRoute])],
  declarations: [LedgerUpdateComponent],
})
export class LedgerUpdateModule {}
