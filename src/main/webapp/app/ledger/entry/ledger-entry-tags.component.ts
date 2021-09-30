/* eslint-disable @typescript-eslint/member-ordering */
/* eslint-disable no-debugger */
/* eslint-disable no-console */
import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { AlertService } from 'app/core/util/alert.service';
import { removeSeparator as tagRemoveSeparator } from 'app/entities/tag/tag-utils';
import { loading, toSubject } from 'app/support/observable';
import LRUCache from 'lru-cache';
import { TagInputComponent } from 'ngx-chips';
import { EMPTY, Observable, Subject } from 'rxjs';
import { first, map, shareReplay, tap } from 'rxjs/operators';
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

type AutocompleteModel = {
  value: string;
  display: string;
};

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

  editTags?: ITag[];
  editTagsMap?: Map<string, ITag>;
  mode = Mode.Default;
  editState = EditState.Default;
  editTagIndex?: number;
  ledgerService: LedgerService;
  alertService: AlertService;
  autocompleteCallback: (text: string) => Observable<any>;
  autocompleteCache = new LRUCache<string, Observable<AutocompleteModel[]>>({ max: 10, maxAge: 60000 });
  autocompleteInput = new Subject<string>();
  autocompletePendingState?: { subject: Subject<AutocompleteModel[]>; text: string };
  autocompleteMatch = (): boolean => true;

  constructor(ledgerService: LedgerService, alertService: AlertService) {
    this.ledgerService = ledgerService;
    this.alertService = alertService;
    this.autocompleteCallback = this.onAutocomplete.bind(this);
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

  onAutocomplete(text: string): Observable<any> {
    /**
     * The library ngx-chips is fundamentally flawed in the way it handles autocompletion.
     * First, it requests new autocomplete data everytime the input field is focused, even
     * if the value did not change. Second, it does not cache any autocomplete responses.
     * Third, and most importantly, it does not cancel earlier autocomplete requests if a
     * more recent one is under way. This causes the user to see stale autocomplete lists
     * when the observable for an older input completes after the more recent input.
     * The problem is described in more detail here:
     * https://angular-training-guide.rangle.io/http/search_with_switchmap
     *
     * The complex code below is a deperate attempt to compensate for the aforementioned
     * deficiencies of the ngx-library. It's trash. It's almost enough to do a complete
     * switch to angular material because it has the only decent chips implementation.
     */

    text = tagRemoveSeparator(text);

    const pendingState = this.autocompletePendingState;
    if (pendingState !== undefined) {
      if (pendingState.text === text) {
        // A request for this text is already pending.
        return EMPTY;
      }
      // Cancel previous autocomplete. Prevents a bug where the user would see
      // suggestions for an earlier input if its autocomplete completes later.
      pendingState.subject.complete();
      this.autocompletePendingState = undefined;
    }

    const existingNormalizedTexts = this.editTags!.map(t => t.textNormalized);

    // Check if we have a cached result.
    const cacheKey = text + ',' + existingNormalizedTexts.join(',');
    const cachedResult = this.autocompleteCache.get(cacheKey);
    if (cachedResult !== undefined) {
      return cachedResult;
    }

    // Build an observable for the web request to fetch the autocomplete result.
    const observable = this.ledgerService.autocompleteTag(text, existingNormalizedTexts).pipe(
      // We only expect a single autocomplete response.
      first(),

      tap(result => {
        // The autocomplete is no longer pending.
        this.autocompletePendingState = undefined;

        // Overwrite the cached subject with observable. The subject may have been
        // canceled, but the observable was still loaded so we cache it instead of
        // discarding it.
        this.autocompleteCache.set(cacheKey, observable);

        console.log(result);
      }),

      // Map to autocomplete model.
      map(tags => tags.map(t => ({ value: t.textNormalized, display: t.text }))),

      // Share the side effects (the HTTP request and the tap function above)
      // as well as replay the cached result.
      shareReplay(1)
    );

    // Create a subject from the observable. The subject can be canceled by calling
    // complete which prevents ngx-chips from receiving stale autocomplete data.
    const subject = toSubject(observable);

    // Cache the subject so that subsequent autocomplete triggers for the same
    // text will use the same subject.
    this.autocompleteCache.set(cacheKey, observable);

    this.autocompletePendingState = { subject, text };

    return subject;
  }
}
