import { KeyValue } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';

import { AlertService, Alert } from 'app/core/util/alert.service';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'jhi-alert-toast',
  templateUrl: './alert-toast.component.html',
})
export class AlertToastComponent implements OnInit, OnDestroy {
  alerts = new Map<any, Alert>();
  subscriptionAdd?: Subscription;
  subscriptionClose?: Subscription;

  constructor(private alertService: AlertService) {}

  ngOnInit(): void {
    // Only add toasts.
    this.subscriptionAdd = this.alertService
      .getSubjectAdd()
      .pipe(filter(a => !!a.toast))
      .subscribe(a => this.alerts.set(a.id, a));
    this.subscriptionClose = this.alertService
      .getSubjectClose()
      .pipe(filter(a => !!a.toast))
      .subscribe(a => this.alerts.delete(a.id));
  }

  ngOnDestroy(): void {
    if (this.subscriptionAdd) {
      this.subscriptionAdd.unsubscribe();
    }
    if (this.subscriptionClose) {
      this.subscriptionClose.unsubscribe();
    }
  }

  close(entry: KeyValue<any, Alert>): void {
    const alert = entry.value;
    this.alerts.delete(alert.id);
    alert.close?.(this.alertService.get());
  }
}
