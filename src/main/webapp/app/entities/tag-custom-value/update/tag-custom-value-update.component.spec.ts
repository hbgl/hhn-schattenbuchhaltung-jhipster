jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TagCustomValueService } from '../service/tag-custom-value.service';
import { ITagCustomValue, TagCustomValue } from '../tag-custom-value.model';
import { ITagCustomType } from 'app/entities/tag-custom-type/tag-custom-type.model';
import { TagCustomTypeService } from 'app/entities/tag-custom-type/service/tag-custom-type.service';

import { TagCustomValueUpdateComponent } from './tag-custom-value-update.component';

describe('Component Tests', () => {
  describe('TagCustomValue Management Update Component', () => {
    let comp: TagCustomValueUpdateComponent;
    let fixture: ComponentFixture<TagCustomValueUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let tagCustomValueService: TagCustomValueService;
    let tagCustomTypeService: TagCustomTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TagCustomValueUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TagCustomValueUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TagCustomValueUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      tagCustomValueService = TestBed.inject(TagCustomValueService);
      tagCustomTypeService = TestBed.inject(TagCustomTypeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call TagCustomType query and add missing value', () => {
        const tagCustomValue: ITagCustomValue = { id: 456 };
        const type: ITagCustomType = { id: 11136 };
        tagCustomValue.type = type;

        const tagCustomTypeCollection: ITagCustomType[] = [{ id: 2186 }];
        spyOn(tagCustomTypeService, 'query').and.returnValue(of(new HttpResponse({ body: tagCustomTypeCollection })));
        const additionalTagCustomTypes = [type];
        const expectedCollection: ITagCustomType[] = [...additionalTagCustomTypes, ...tagCustomTypeCollection];
        spyOn(tagCustomTypeService, 'addTagCustomTypeToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ tagCustomValue });
        comp.ngOnInit();

        expect(tagCustomTypeService.query).toHaveBeenCalled();
        expect(tagCustomTypeService.addTagCustomTypeToCollectionIfMissing).toHaveBeenCalledWith(
          tagCustomTypeCollection,
          ...additionalTagCustomTypes
        );
        expect(comp.tagCustomTypesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const tagCustomValue: ITagCustomValue = { id: 456 };
        const type: ITagCustomType = { id: 75865 };
        tagCustomValue.type = type;

        activatedRoute.data = of({ tagCustomValue });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(tagCustomValue));
        expect(comp.tagCustomTypesSharedCollection).toContain(type);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tagCustomValue = { id: 123 };
        spyOn(tagCustomValueService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tagCustomValue });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tagCustomValue }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(tagCustomValueService.update).toHaveBeenCalledWith(tagCustomValue);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tagCustomValue = new TagCustomValue();
        spyOn(tagCustomValueService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tagCustomValue });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tagCustomValue }));
        saveSubject.complete();

        // THEN
        expect(tagCustomValueService.create).toHaveBeenCalledWith(tagCustomValue);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tagCustomValue = { id: 123 };
        spyOn(tagCustomValueService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tagCustomValue });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(tagCustomValueService.update).toHaveBeenCalledWith(tagCustomValue);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackTagCustomTypeById', () => {
        it('Should return tracked TagCustomType primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTagCustomTypeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
