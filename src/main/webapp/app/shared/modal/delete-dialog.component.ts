import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  templateUrl: './delete-dialog.component.html',
})
export class DeleteDialogComponent {
  @Input() titleTransKey = '';
  @Input() titleTransValues = {};
  @Input() bodyTransKey = '';
  @Input() bodyTransValues = {};

  constructor(public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss('cancel');
  }

  confirm(): void {
    this.activeModal.close('delete');
  }
}
