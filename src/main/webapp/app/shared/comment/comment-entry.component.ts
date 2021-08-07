import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChange, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { Authority } from 'app/config/authority.constants';
import { AccountService } from 'app/core/auth/account.service';
import { uniqueId } from 'app/core/util/unique-id';
import { CommentCollapseState } from 'app/db/comment-collapse-state';
import { IndexedDbService } from 'app/db/indexed-db.service';
import { Comment } from 'app/shared/comment/comment.model';
import { combineLatest, Subject, Subscription, timer, zip } from 'rxjs';
import { distinctUntilChanged, startWith } from 'rxjs/operators';
import { DeleteDialogComponent } from '../modal/delete-dialog.component';
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing';
import { CommentService } from './comment.service';

class Action {
  constructor(public name: string, public run: () => void) {}
}

type State = 'idle' | 'deletePrompt' | 'deleting' | 'editing' | 'updating';

@Component({
  selector: 'jhi-comment-entry',
  templateUrl: './comment-entry.component.html',
  styleUrls: ['./comment-entry.scss'],
})
export class CommentEntryComponent implements OnChanges, OnDestroy {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('comment') commentInput?: Comment;
  @Input() ledgerEntryNo?: string;
  @Output() commentChange = new EventEmitter<Comment>();
  @Output() commentDeleted = new EventEmitter<Comment>();

  comment?: Comment;
  actions: Action[] = [];
  collapsed?: boolean;
  state: State = 'idle';
  componentId = uniqueId();

  commentService: CommentService;
  translateService: TranslateService;
  accountService: AccountService;
  modalService: NgbModal;
  db: IndexedDbService;

  collapseSubject = new Subject<CommentCollapseState>();
  commentSubject = new Subject<Comment | undefined>();
  subscriptions: Subscription[] = [];

  constructor(
    db: IndexedDbService,
    commentService: CommentService,
    accountService: AccountService,
    translateService: TranslateService,
    modalService: NgbModal
  ) {
    this.db = db;
    this.commentService = commentService;
    this.translateService = translateService;
    this.accountService = accountService;
    this.modalService = modalService;

    this.initActionRefresh();
    this.initCollapseStateLoad();
    this.initCollapseStateSave();
  }

  private initActionRefresh(): void {
    // Refresh actions when the account, comment, or language changes.
    const sub = combineLatest([
      this.accountService.identity().pipe(startWith(null)),
      this.commentSubject.pipe(startWith(null)),
      this.translateService.onLangChange.pipe(startWith(null)),
    ]).subscribe(([account, comment]) => {
      const isAuthorOrAdmin = account && comment && (account.id === comment.author.id || account.hasAnyAuthority(Authority.ADMIN));

      if (isAuthorOrAdmin) {
        this.actions = [
          new Action(this.translateService.instant('entity.action.edit'), () => this.edit()),
          new Action(this.translateService.instant('entity.action.delete'), () => {
            this.delete();
          }),
        ];
      } else {
        this.actions = [];
      }
    });
    this.subscriptions.push(sub);
  }

  private initCollapseStateSave(): void {
    // Save the latest collapse state.
    const sub = this.collapseSubject
      .pipe(
        distinctUntilChanged((a, b) => a.equals(b)),
        exhaustMapWithTrailing(cs => this.db.commentCollapseStates.put(cs, cs.id))
      )
      .subscribe();
    this.subscriptions.push(sub);
  }

  private initCollapseStateLoad(): void {
    // Load the collapse state for the latest comment.
    const sub = this.commentSubject
      .pipe(
        distinctUntilChanged(),
        exhaustMapWithTrailing(async c => {
          if (!c) {
            return;
          }
          const state = await this.db.commentCollapseStates.get(c.id);
          this.collapsed = state ? state.collapsed : false;
        })
      )
      .subscribe();
    this.subscriptions.push(sub);
  }

  ngOnDestroy(): void {
    for (const sub of this.subscriptions) {
      sub.unsubscribe();
    }
  }

  ngOnChanges(changes: Record<string, SimpleChange | undefined>): void {
    const commentChange = changes.commentInput;
    if (commentChange) {
      this.comment = commentChange.currentValue;
      this.commentSubject.next(commentChange.currentValue);
    }
  }

  toggleCollapse(): void {
    if (this.collapsed === undefined || this.comment === undefined) {
      return;
    }
    this.collapsed = !this.collapsed;
    this.collapseSubject.next(new CommentCollapseState(this.comment.id, this.collapsed));
  }

  actionId(_index: number, action: Action): string {
    return action.name;
  }

  edit(): void {
    this.state = 'editing';
  }

  get isEditFormVisible(): boolean {
    return this.state === 'editing' || this.state === 'updating';
  }

  editCanceled(): void {
    this.state = 'idle';
  }

  editCommentSaved(comment: Comment): void {
    this.comment = comment;
    this.commentChange.emit(comment);
    this.state = 'idle';
  }

  async delete(): Promise<void> {
    if (this.state === 'deletePrompt') {
      // Don't show delete modal twice.
      return;
    }
    try {
      this.state = 'deletePrompt';

      // Show modal.
      const modalRef = this.modalService.open(DeleteDialogComponent, { size: 'lg', backdrop: 'static' });
      modalRef.componentInstance.titleTransKey = 'entity.delete.title';
      modalRef.componentInstance.bodyTransKey = 'schattenbuchhaltungApp.comment.delete.question';

      // Wait for modal to close.
      await modalRef.result;

      this.state = 'deleting';

      // Wait at least 500ms to not make the UI look smoother.
      await zip(this.commentService.delete(this.comment!.id), timer(500)).toPromise();

      this.commentDeleted.emit(this.comment);
    } catch (e) {
      // Ignore modal dismissed
    } finally {
      this.state = 'idle';
    }
  }
}
