import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';

import { LedgerImportComponent } from './ledger-import.component';
import { importRoute } from './ledger-import.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([importRoute])],
  declarations: [LedgerImportComponent],
})
export class LedgerImportModule {}
