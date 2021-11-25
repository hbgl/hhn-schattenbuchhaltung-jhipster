import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';

import { LedgerMetaImportComponent } from './meta-import.component';
import { metaExportRoute } from './meta-import.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([metaExportRoute])],
  declarations: [LedgerMetaImportComponent],
})
export class LedgerMetaImportModule {}
