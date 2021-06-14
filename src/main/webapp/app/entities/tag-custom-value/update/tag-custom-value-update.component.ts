import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITagCustomValue, TagCustomValue } from '../tag-custom-value.model';
import { TagCustomValueService } from '../service/tag-custom-value.service';
import { ITagCustomType } from 'app/entities/tag-custom-type/tag-custom-type.model';
import { TagCustomTypeService } from 'app/entities/tag-custom-type/service/tag-custom-type.service';

@Component({
  selector: 'jhi-tag-custom-value-update',
  templateUrl: './tag-custom-value-update.component.html',
})
export class TagCustomValueUpdateComponent implements OnInit {
  isSaving = false;

  tagCustomTypesSharedCollection: ITagCustomType[] = [];

  editForm = this.fb.group({
    id: [],
    value: [null, [Validators.required]],
    type: [null, Validators.required],
  });

  constructor(
    protected tagCustomValueService: TagCustomValueService,
    protected tagCustomTypeService: TagCustomTypeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tagCustomValue }) => {
      this.updateForm(tagCustomValue);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tagCustomValue = this.createFromForm();
    if (tagCustomValue.id !== undefined) {
      this.subscribeToSaveResponse(this.tagCustomValueService.update(tagCustomValue));
    } else {
      this.subscribeToSaveResponse(this.tagCustomValueService.create(tagCustomValue));
    }
  }

  trackTagCustomTypeById(index: number, item: ITagCustomType): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITagCustomValue>>): void {
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

  protected updateForm(tagCustomValue: ITagCustomValue): void {
    this.editForm.patchValue({
      id: tagCustomValue.id,
      value: tagCustomValue.value,
      type: tagCustomValue.type,
    });

    this.tagCustomTypesSharedCollection = this.tagCustomTypeService.addTagCustomTypeToCollectionIfMissing(
      this.tagCustomTypesSharedCollection,
      tagCustomValue.type
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tagCustomTypeService
      .query()
      .pipe(map((res: HttpResponse<ITagCustomType[]>) => res.body ?? []))
      .pipe(
        map((tagCustomTypes: ITagCustomType[]) =>
          this.tagCustomTypeService.addTagCustomTypeToCollectionIfMissing(tagCustomTypes, this.editForm.get('type')!.value)
        )
      )
      .subscribe((tagCustomTypes: ITagCustomType[]) => (this.tagCustomTypesSharedCollection = tagCustomTypes));
  }

  protected createFromForm(): ITagCustomValue {
    return {
      ...new TagCustomValue(),
      id: this.editForm.get(['id'])!.value,
      value: this.editForm.get(['value'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
