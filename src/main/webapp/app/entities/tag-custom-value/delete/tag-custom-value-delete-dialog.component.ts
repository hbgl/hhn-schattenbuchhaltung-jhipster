import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITagCustomValue } from '../tag-custom-value.model';
import { TagCustomValueService } from '../service/tag-custom-value.service';

@Component({
  templateUrl: './tag-custom-value-delete-dialog.component.html',
})
export class TagCustomValueDeleteDialogComponent {
  tagCustomValue?: ITagCustomValue;

  constructor(protected tagCustomValueService: TagCustomValueService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tagCustomValueService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
