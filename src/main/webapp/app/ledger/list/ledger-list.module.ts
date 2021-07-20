import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';

import { LedgerListComponent } from './ledger-list.component';
import { listRoute } from './ledger-list.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([listRoute])],
  declarations: [LedgerListComponent],
})
export class LedgerListModule {}
