/* eslint-disable @typescript-eslint/member-ordering */
/* eslint-disable no-debugger */
/* eslint-disable no-console */
import { ChangeDetectorRef, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { AlertService } from 'app/core/util/alert.service';
import { loading } from 'app/support/observable';
import { TagInputComponent } from 'ngx-chips';
import { ITag, Tag } from '../ledger-entry.model';
import { LedgerService } from '../ledger.service';

enum Mode {
  Default = 'default',
  Editing = 'editing',
}

enum EditState {
  Default = 'default',
  Saving = 'saving',
}

@Component({
  selector: 'jhi-ledger-entry-tags',
  templateUrl: './ledger-entry-tags.component.html',
  styleUrls: ['./ledger-entry-tags.component.scss'],
})
export class LedgerEntryTagsComponent {
  @Input() ledgerEntryNo?: string;
  @Input() tags?: Tag[];
  @Output() tagsChange = new EventEmitter<Tag[]>();
  @ViewChild('tagInput') tagInput?: TagInputComponent;

  items = ['123', '456'];
  editTags?: ITag[];
  editTagsMap?: Map<string, ITag>;
  mode = Mode.Default;
  editState = EditState.Default;
  editTagIndex?: number;
  ledgerService: LedgerService;
  alertService: AlertService;
  cd: ChangeDetectorRef;

  constructor(ledgerService: LedgerService, alertService: AlertService, cd: ChangeDetectorRef) {
    this.ledgerService = ledgerService;
    this.alertService = alertService;
    this.cd = cd;
  }

  get isDefaultMode(): boolean {
    return this.mode === Mode.Default;
  }

  get isEditMode(): boolean {
    return this.mode === Mode.Editing;
  }

  get canEdit(): boolean {
    return this.isEditMode && this.editState === EditState.Default;
  }

  get isSaving(): boolean {
    return this.isEditMode && this.editState === EditState.Saving;
  }

  trackTag(index: number, tag: ITag): string {
    return tag.id.toString();
  }

  onEnterEditMode(): void {
    if (this.tags === undefined || !this.isDefaultMode) {
      return;
    }
    // The ngx-chips library does not play well with objects that have setters
    // because it internally converts all objects to their own model.
    // That is why we need to convert the tags to a pojo.
    this.editTags = this.tags.map(t => t.toPojo());
    this.editTagsMap = new Map();
    for (const tag of this.editTags) {
      this.editTagsMap.set(tag.textNormalized, tag);
    }
    this.mode = Mode.Editing;
  }

  onCancelEditMode(): void {
    if (!this.canEdit) {
      return;
    }
    this.mode = Mode.Default;
    this.editTags = undefined;
    this.editTagsMap = undefined;
  }

  async onSaveEditMode(): Promise<void> {
    if (!this.canEdit) {
      return;
    }

    const tagInput = this.tagInput!;
    if (tagInput.hasErrors()) {
      return;
    }

    // Submit current value in input.
    const input = tagInput.inputForm.input.nativeElement as HTMLInputElement;
    input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter' }));

    const editedTags = this.editTags!;
    const editedTagSet = new Set<string>(editedTags.map(t => t.textNormalized));
    const editedTagTexts = editedTags.map(t => t.text);

    const existingTags = this.tags!;
    const deleteTagTexts = existingTags.filter(t => !editedTagSet.has(t.textNormalized)).map(t => t.text);

    let tags = [];
    try {
      this.editState = EditState.Saving;
      tags = await loading(this.ledgerService.updateTags(this.ledgerEntryNo!, editedTagTexts, deleteTagTexts)).toPromise();
    } catch (e) {
      // Handled by middleware.
      return;
    } finally {
      this.editState = EditState.Default;
    }

    this.tags = tags;
    this.mode = Mode.Default;
    this.editTags = undefined;
    this.editTagsMap = undefined;
    this.tagsChange.emit(tags);
  }
}
