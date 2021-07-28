import { Route } from '@angular/router';

import { LedgerEntryComponent } from './ledger-entry.component';

export const entryRoute: Route = {
  path: '',
  component: LedgerEntryComponent,
  data: {
    pageTitle: 'schattenbuchhaltungApp.ledger.entry.title',
  },
};
