import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITagCustomValue } from '../tag-custom-value.model';

@Component({
  selector: 'jhi-tag-custom-value-detail',
  templateUrl: './tag-custom-value-detail.component.html',
})
export class TagCustomValueDetailComponent implements OnInit {
  tagCustomValue: ITagCustomValue | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tagCustomValue }) => {
      this.tagCustomValue = tagCustomValue;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
