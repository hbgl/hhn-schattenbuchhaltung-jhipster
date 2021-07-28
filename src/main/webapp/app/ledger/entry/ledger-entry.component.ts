import { Component, OnInit } from '@angular/core';
import { LedgerService } from '../ledger.service';
import { Comment, LedgerEntryDetail, Tag } from '../ledger-entry.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'jhi-ledger-entry',
  templateUrl: './ledger-entry.component.html',
  styleUrls: ['./ledger-entry.component.scss'],
})
export class LedgerEntryComponent implements OnInit {
  entry?: LedgerEntryDetail;

  constructor(protected ledgerService: LedgerService, protected route: ActivatedRoute) {}

  ngOnInit(): void {
    this.handleNavigation();
  }

  trackTag(index: number, tag: Tag): string {
    return tag.id.toString();
  }

  trackComment(index: number, comment: Comment): number {
    return comment.id;
  }

  protected handleNavigation(): void {
    this.route.params.subscribe(params => {
      this.ledgerService.detail(params['no']).subscribe(entry => {
        this.entry = entry;
      });
    });
  }
}
