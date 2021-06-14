import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITagCustomType, TagCustomType } from '../tag-custom-type.model';
import { TagCustomTypeService } from '../service/tag-custom-type.service';

@Component({
  selector: 'jhi-tag-custom-type-update',
  templateUrl: './tag-custom-type-update.component.html',
})
export class TagCustomTypeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    enabled: [null, [Validators.required]],
  });

  constructor(protected tagCustomTypeService: TagCustomTypeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tagCustomType }) => {
      this.updateForm(tagCustomType);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tagCustomType = this.createFromForm();
    if (tagCustomType.id !== undefined) {
      this.subscribeToSaveResponse(this.tagCustomTypeService.update(tagCustomType));
    } else {
      this.subscribeToSaveResponse(this.tagCustomTypeService.create(tagCustomType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITagCustomType>>): void {
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

  protected updateForm(tagCustomType: ITagCustomType): void {
    this.editForm.patchValue({
      id: tagCustomType.id,
      name: tagCustomType.name,
      enabled: tagCustomType.enabled,
    });
  }

  protected createFromForm(): ITagCustomType {
    return {
      ...new TagCustomType(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      enabled: this.editForm.get(['enabled'])!.value,
    };
  }
}
