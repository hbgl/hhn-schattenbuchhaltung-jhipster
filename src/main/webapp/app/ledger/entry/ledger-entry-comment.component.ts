import { Component, Input, OnChanges, SimpleChange } from '@angular/core';
import { CommentCollapseState } from 'app/db/comment-collapse-state';
import { IndexedDbService } from 'app/db/indexed-db.service';
import { Comment } from '../ledger-entry.model';

@Component({
  selector: 'jhi-ledger-entry-comment',
  templateUrl: './ledger-entry-comment.component.html',
  styleUrls: ['./ledger-entry-comment.scss'],
})
export class LedgerEntryCommentComponent implements OnChanges {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('comment') commentInput?: Comment;
  comment?: Comment;
  collapsed?: boolean;
  collapsedSavePromise: Map<number, Promise<any>>;
  collapseGetPromise?: Promise<any>;
  db: IndexedDbService;

  constructor(db: IndexedDbService) {
    this.db = db;
    this.collapsedSavePromise = new Map();
  }

  // eslint-disable-next-line @typescript-eslint/require-await
  async ngOnChanges(changes: Record<string, SimpleChange | undefined>): Promise<void> {
    const commentChange = changes.commentInput;
    if (commentChange) {
      this.onCommentChanged(commentChange);
    }
  }

  async toggleCollapse(): Promise<void> {
    // TODO: Maybe use ngrx to handle global comment state. That opens another can of worms though.
    // The implementation is probably overkill because it tracks reentrance and changing comments.

    if (this.collapsed === undefined) {
      return;
    }

    let collapsed = !this.collapsed; // New collapse state
    this.collapsed = collapsed; // Update local state

    if (this.comment === undefined) {
      return;
    }

    const commentId = this.comment.id; // Current comment ID

    // Now save the state in the IndexedDB.
    if (this.collapsedSavePromise.has(commentId)) {
      // Saving is already in progress for this comment ID.
      return;
    }

    // Save the current collapsed state. If it was changed after it had been saved,
    // then save it again.
    try {
      for (;;) {
        const collapseState = new CommentCollapseState(commentId, collapsed);
        const promise = this.db.commentCollapseStates.put(collapseState, collapseState.id);
        this.collapsedSavePromise.set(commentId, promise);
        await promise;

        // Break if the comment changed.
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (this.comment === undefined || this.comment.id !== commentId) {
          break;
        }

        // Break if the saved state is the current state.
        if (collapsed === this.collapsed) {
          break;
        }

        collapsed = this.collapsed;
      }
    } finally {
      this.collapsedSavePromise.delete(commentId);
    }
  }

  private async onCommentChanged(commentChange: SimpleChange): Promise<void> {
    if (this.collapseGetPromise) {
      // There is already a async handler reading the collapse state.
      return;
    }

    let comment: Comment | undefined = commentChange.currentValue;

    let collapsed;
    try {
      for (;;) {
        if (comment === undefined) {
          collapsed = undefined;
          break;
        }

        // Read collapse state from IndexedDB.
        const promise = this.db.commentCollapseStates.get(comment.id);
        this.collapseGetPromise = promise;
        const state = await promise;
        collapsed = state ? state.collapsed : false;

        // If the input comment did not change, we are done.
        if (this.commentInput === comment) {
          break;
        }

        // The input comment changed. Continue.
        comment = this.commentInput;
      }
    } finally {
      this.collapseGetPromise = undefined;
    }

    this.comment = comment;
    this.collapsed = collapsed;
  }
}
