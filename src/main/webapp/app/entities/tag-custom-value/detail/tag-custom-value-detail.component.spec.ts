import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TagCustomValueDetailComponent } from './tag-custom-value-detail.component';

describe('Component Tests', () => {
  describe('TagCustomValue Management Detail Component', () => {
    let comp: TagCustomValueDetailComponent;
    let fixture: ComponentFixture<TagCustomValueDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TagCustomValueDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ tagCustomValue: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(TagCustomValueDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TagCustomValueDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load tagCustomValue on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.tagCustomValue).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
