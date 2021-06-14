jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TagCustomTypeService } from '../service/tag-custom-type.service';
import { ITagCustomType, TagCustomType } from '../tag-custom-type.model';

import { TagCustomTypeUpdateComponent } from './tag-custom-type-update.component';

describe('Component Tests', () => {
  describe('TagCustomType Management Update Component', () => {
    let comp: TagCustomTypeUpdateComponent;
    let fixture: ComponentFixture<TagCustomTypeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let tagCustomTypeService: TagCustomTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TagCustomTypeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TagCustomTypeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TagCustomTypeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      tagCustomTypeService = TestBed.inject(TagCustomTypeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const tagCustomType: ITagCustomType = { id: 456 };

        activatedRoute.data = of({ tagCustomType });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(tagCustomType));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tagCustomType = { id: 123 };
        spyOn(tagCustomTypeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tagCustomType });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tagCustomType }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(tagCustomTypeService.update).toHaveBeenCalledWith(tagCustomType);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tagCustomType = new TagCustomType();
        spyOn(tagCustomTypeService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tagCustomType });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tagCustomType }));
        saveSubject.complete();

        // THEN
        expect(tagCustomTypeService.create).toHaveBeenCalledWith(tagCustomType);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tagCustomType = { id: 123 };
        spyOn(tagCustomTypeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tagCustomType });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(tagCustomTypeService.update).toHaveBeenCalledWith(tagCustomType);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
