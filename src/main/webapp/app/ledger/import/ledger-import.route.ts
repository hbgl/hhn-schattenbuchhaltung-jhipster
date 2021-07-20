import { Route } from '@angular/router';

import { LedgerImportComponent } from './ledger-import.component';

export const importRoute: Route = {
  path: '',
  component: LedgerImportComponent,
  data: {
    pageTitle: 'schattenbuchhaltungApp.ledger.import.title',
  },
};
