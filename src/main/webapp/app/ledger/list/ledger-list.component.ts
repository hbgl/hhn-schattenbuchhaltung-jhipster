import { Component, OnInit } from '@angular/core';
import { LedgerListEntry } from '../ledger-entry.model';
import { LedgerService } from '../ledger.service';

@Component({
  selector: 'jhi-ledger-list',
  templateUrl: './ledger-list.component.html',
  styleUrls: ['./ledger-list.component.scss'],
})
export class LedgerListComponent implements OnInit {
  entries?: LedgerListEntry[];

  constructor(protected ledgerService: LedgerService) {}

  ngOnInit(): Promise<void> {
    return this.handleNavigation();
  }

  trackId(index: number, entry: LedgerListEntry): string {
    return entry.no;
  }

  protected async handleNavigation(): Promise<void> {
    this.entries = await this.ledgerService.list().toPromise();
  }
}
