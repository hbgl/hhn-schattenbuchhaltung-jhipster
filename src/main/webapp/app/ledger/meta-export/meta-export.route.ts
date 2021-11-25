import { Route } from '@angular/router';

import { LedgerMetaExportComponent } from './meta-export.component';

export const metaExportRoute: Route = {
  path: '',
  component: LedgerMetaExportComponent,
  data: {
    pageTitle: 'schattenbuchhaltungApp.ledger.metaExport.title',
  },
};
