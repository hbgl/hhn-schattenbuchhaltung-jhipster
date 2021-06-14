import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITagCustomType } from '../tag-custom-type.model';

@Component({
  selector: 'jhi-tag-custom-type-detail',
  templateUrl: './tag-custom-type-detail.component.html',
})
export class TagCustomTypeDetailComponent implements OnInit {
  tagCustomType: ITagCustomType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tagCustomType }) => {
      this.tagCustomType = tagCustomType;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
