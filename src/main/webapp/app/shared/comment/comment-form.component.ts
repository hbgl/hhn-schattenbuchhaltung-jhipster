import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChange, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from 'app/core/util/alert.service';
import { loading } from 'app/support/observable';
import escapeHTML from 'escape-html';
import { Comment, CommentIn } from './comment.model';
import { CommentService } from './comment.service';

export type CommentFormType = 'create' | 'edit';

@Component({
  selector: 'jhi-comment-form',
  templateUrl: './comment-form.component.html',
})
export class CommentFormComponent implements OnChanges, AfterViewInit {
  @Input() commentId: number | null = null;
  @Input() parentId: number | null = null;
  @Input() ledgerEntryNo?: string;
  @Input() contentHtml = '';
  @Input() type: CommentFormType = 'create';
  @Output() canceled = new EventEmitter<void>();
  @Output() commentSaved = new EventEmitter<Comment>();
  @Output() contentHtmlChange = new EventEmitter<string>();
  @ViewChild('contentTextarea') contentTextarea?: ElementRef<HTMLTextAreaElement>;

  originalContentHtml = '';
  _isSubmitting = false;

  form = this.fb.group({
    content: [null, [Validators.required]],
  });

  constructor(
    protected alertService: AlertService,
    protected commentService: CommentService,
    protected translationService: TranslateService,
    protected fb: FormBuilder
  ) {
    const contentField = this.form.get('content')!;
    contentField;
    contentField.valueChanges.subscribe((newVal: any) => {
      if (this.contentHtml !== newVal) {
        // Propagate changes.
        this.contentHtml = newVal;
        this.contentHtmlChange.emit(newVal);
        // Mark content field as pristine if cleared to avoid showing
        // invalid indicators.
        if (newVal === '') {
          contentField.markAsPristine();
        }
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.type === 'edit') {
      this.contentTextarea?.nativeElement.focus();
    }
  }

  ngOnChanges(changes: Record<string, SimpleChange | undefined>): void {
    if (changes.contentHtml) {
      // Save orignal comment content. When editing, do not allow user to submit
      // unless the content was changed.
      this.originalContentHtml = changes.contentHtml.currentValue;

      // Propagate changes.
      this.form.setValue({ content: changes.contentHtml.currentValue });
    }
  }

  cancel(): void {
    this.form.reset();
    this.form.markAsPristine();
    this.canceled.emit();
  }

  async save(): Promise<void> {
    if (this.form.invalid) {
      const contentField = this.form.get('content')!;
      if (contentField.invalid) {
        contentField.markAsDirty();
      }
      return;
    }
    try {
      this.isSubmitting = true;
      const dto = new CommentIn(this.parentId, this.ledgerEntryNo!, escapeHTML(this.form.get('content')!.value));

      let observable;
      if (this.type === 'edit') {
        observable = this.commentService.update(this.commentId!, dto);
      } else {
        observable = this.commentService.create(dto);
      }

      // Wait at least 500ms to not make the UI look smoother.
      const response = await loading(observable).toPromise();

      this.commentSaved.emit(response);

      // await new Promise((res) => setTimeout(() => res(0), 1000));
      // const comment = new Comment(123, dto.contentHtml, '2021-08-04T10:00:00Z', null, new User('123', 'Foo', 'Bar', 'foobar@hbgl.dev', null));
      // this.commentSaved.emit(comment);
    } finally {
      this.isSubmitting = false;
    }

    this.reset();
  }

  set isSubmitting(value: boolean) {
    this._isSubmitting = value;
    if (value) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  get isSubmitting(): boolean {
    return this._isSubmitting;
  }

  reset(): void {
    this.form.reset();
    this.form.markAsPristine();
  }
}
