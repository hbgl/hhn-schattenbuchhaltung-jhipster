import { Route } from '@angular/router';

import { LedgerMetaImportComponent } from './meta-import.component';

export const metaExportRoute: Route = {
  path: '',
  component: LedgerMetaImportComponent,
  data: {
    pageTitle: 'schattenbuchhaltungApp.ledger.metaImport.title',
  },
};
