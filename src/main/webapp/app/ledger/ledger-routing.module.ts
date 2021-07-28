import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'import',
        loadChildren: () => import('./import/ledger-import.module').then(m => m.LedgerImportModule),
      },
      {
        path: '',
        loadChildren: () => import('./list/ledger-list.module').then(m => m.LedgerListModule),
      },
      {
        path: 'entry/:no',
        loadChildren: () => import('./entry/ledger-entry.module').then(m => m.LedgerEntryModule),
      },
    ]),
  ],
})
export class LedgerRoutingModule {}
