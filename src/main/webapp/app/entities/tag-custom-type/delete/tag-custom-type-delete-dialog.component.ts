import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITagCustomType } from '../tag-custom-type.model';
import { TagCustomTypeService } from '../service/tag-custom-type.service';

@Component({
  templateUrl: './tag-custom-type-delete-dialog.component.html',
})
export class TagCustomTypeDeleteDialogComponent {
  tagCustomType?: ITagCustomType;

  constructor(protected tagCustomTypeService: TagCustomTypeService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tagCustomTypeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
