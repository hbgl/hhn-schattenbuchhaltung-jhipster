import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TagCustomTypeDetailComponent } from './tag-custom-type-detail.component';

describe('Component Tests', () => {
  describe('TagCustomType Management Detail Component', () => {
    let comp: TagCustomTypeDetailComponent;
    let fixture: ComponentFixture<TagCustomTypeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TagCustomTypeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ tagCustomType: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(TagCustomTypeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TagCustomTypeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load tagCustomType on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.tagCustomType).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
