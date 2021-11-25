import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'update',
        loadChildren: () => import('./update/ledger-update.module').then(m => m.LedgerUpdateModule),
      },
      {
        path: '',
        loadChildren: () => import('./list/ledger-list.module').then(m => m.LedgerListModule),
      },
      {
        path: 'entry/:no',
        loadChildren: () => import('./entry/ledger-entry.module').then(m => m.LedgerEntryModule),
      },
      {
        path: 'meta/export',
        loadChildren: () => import('./meta-export/meta-export.module').then(m => m.LedgerMetaExportModule),
      },
      {
        path: 'meta/import',
        loadChildren: () => import('./meta-import/meta-import.module').then(m => m.LedgerMetaImportModule),
      },
    ]),
  ],
})
export class LedgerRoutingModule {}
