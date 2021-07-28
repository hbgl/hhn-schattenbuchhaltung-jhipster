import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITag, Tag } from '../tag.model';
import { TagService } from '../service/tag.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ITagCustomType } from 'app/entities/tag-custom-type/tag-custom-type.model';
import { TagCustomTypeService } from 'app/entities/tag-custom-type/service/tag-custom-type.service';
import { ITagCustomValue } from 'app/entities/tag-custom-value/tag-custom-value.model';
import { TagCustomValueService } from 'app/entities/tag-custom-value/service/tag-custom-value.service';

@Component({
  selector: 'jhi-tag-update',
  templateUrl: './tag-update.component.html',
})
export class TagUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  tagCustomTypesSharedCollection: ITagCustomType[] = [];
  tagCustomValuesSharedCollection: ITagCustomValue[] = [];

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    text: [],
    person: [],
    customType: [],
    customValue: [],
  });

  constructor(
    protected tagService: TagService,
    protected userService: UserService,
    protected tagCustomTypeService: TagCustomTypeService,
    protected tagCustomValueService: TagCustomValueService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tag }) => {
      this.updateForm(tag);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tag = this.createFromForm();
    if (tag.id !== undefined) {
      this.subscribeToSaveResponse(this.tagService.update(tag));
    } else {
      this.subscribeToSaveResponse(this.tagService.create(tag));
    }
  }

  trackUserById(index: number, item: IUser): string {
    return item.id!;
  }

  trackTagCustomTypeById(index: number, item: ITagCustomType): number {
    return item.id!;
  }

  trackTagCustomValueById(index: number, item: ITagCustomValue): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITag>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(tag: ITag): void {
    this.editForm.patchValue({
      id: tag.id,
      type: tag.type,
      text: tag.text,
      person: tag.person,
      customType: tag.customType,
      customValue: tag.customValue,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, tag.person);
    this.tagCustomTypesSharedCollection = this.tagCustomTypeService.addTagCustomTypeToCollectionIfMissing(
      this.tagCustomTypesSharedCollection,
      tag.customType
    );
    this.tagCustomValuesSharedCollection = this.tagCustomValueService.addTagCustomValueToCollectionIfMissing(
      this.tagCustomValuesSharedCollection,
      tag.customValue
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('person')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.tagCustomTypeService
      .query()
      .pipe(map((res: HttpResponse<ITagCustomType[]>) => res.body ?? []))
      .pipe(
        map((tagCustomTypes: ITagCustomType[]) =>
          this.tagCustomTypeService.addTagCustomTypeToCollectionIfMissing(tagCustomTypes, this.editForm.get('customType')!.value)
        )
      )
      .subscribe((tagCustomTypes: ITagCustomType[]) => (this.tagCustomTypesSharedCollection = tagCustomTypes));

    this.tagCustomValueService
      .query()
      .pipe(map((res: HttpResponse<ITagCustomValue[]>) => res.body ?? []))
      .pipe(
        map((tagCustomValues: ITagCustomValue[]) =>
          this.tagCustomValueService.addTagCustomValueToCollectionIfMissing(tagCustomValues, this.editForm.get('customValue')!.value)
        )
      )
      .subscribe((tagCustomValues: ITagCustomValue[]) => (this.tagCustomValuesSharedCollection = tagCustomValues));
  }

  protected createFromForm(): ITag {
    return {
      ...new Tag(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      text: this.editForm.get(['text'])!.value,
      person: this.editForm.get(['person'])!.value,
      customType: this.editForm.get(['customType'])!.value,
      customValue: this.editForm.get(['customValue'])!.value,
    };
  }
}
