import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';

import { LedgerMetaExportComponent } from './meta-export.component';
import { metaExportRoute } from './meta-export.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([metaExportRoute])],
  declarations: [LedgerMetaExportComponent],
})
export class LedgerMetaExportModule {}
