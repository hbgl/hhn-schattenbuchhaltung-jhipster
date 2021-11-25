import { Route } from '@angular/router';

import { LedgerUpdateComponent } from './ledger-update.component';

export const importRoute: Route = {
  path: '',
  component: LedgerUpdateComponent,
  data: {
    pageTitle: 'schattenbuchhaltungApp.ledger.import.title',
  },
};
