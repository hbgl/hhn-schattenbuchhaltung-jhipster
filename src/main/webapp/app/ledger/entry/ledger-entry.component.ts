/* eslint-disable no-console */
import { Component, OnInit } from '@angular/core';
import { LedgerService } from '../ledger.service';
import { LedgerEntryDetail, Tag } from '../ledger-entry.model';
import { ActivatedRoute } from '@angular/router';
import { Comment } from 'app/shared/comment/comment.model';

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

  commentCreated(comment: Comment): void {
    if (this.entry) {
      this.entry.comments.unshift(comment);
    }
  }

  commentDeleted(comment: Comment): void {
    if (this.entry) {
      const index = this.entry.comments.findIndex(c => c.id === comment.id);
      if (index >= 0) {
        this.entry.comments.splice(index, 1);
      }
    }
  }

  protected handleNavigation(): void {
    this.route.params.subscribe(params => {
      this.ledgerService.detail(params['no']).subscribe(entry => {
        this.entry = entry;
      });
    });
  }
}
