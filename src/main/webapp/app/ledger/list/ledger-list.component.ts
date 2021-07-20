import { Component, OnInit } from '@angular/core';
import { LedgerEntry } from '../ledger-entry.model';
import { LedgerListService } from './ledger-list.service';

@Component({
  selector: 'jhi-ledger-list',
  templateUrl: './ledger-list.component.html',
  styleUrls: ['./ledger-list.component.scss'],
})
export class LedgerListComponent implements OnInit {
  entries: LedgerEntry[] = [];

  constructor(protected ledgerListService: LedgerListService) {}

  ngOnInit(): Promise<void> {
    return this.handleNavigation();
  }

  trackId(index: number, entry: LedgerEntry): string {
    return entry.no;
  }

  protected async handleNavigation(): Promise<void> {
    this.entries = await this.ledgerListService.ledger().toPromise();
  }
}
