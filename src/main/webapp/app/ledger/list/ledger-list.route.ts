import { Route } from '@angular/router';

import { LedgerListComponent } from './ledger-list.component';

export const listRoute: Route = {
  path: '',
  component: LedgerListComponent,
  data: {
    pageTitle: 'schattenbuchhaltungApp.ledger.home.title',
  },
};
