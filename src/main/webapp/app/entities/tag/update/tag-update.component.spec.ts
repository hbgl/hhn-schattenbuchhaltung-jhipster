jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TagService } from '../service/tag.service';
import { ITag, Tag } from '../tag.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ITagCustomType } from 'app/entities/tag-custom-type/tag-custom-type.model';
import { TagCustomTypeService } from 'app/entities/tag-custom-type/service/tag-custom-type.service';
import { ITagCustomValue } from 'app/entities/tag-custom-value/tag-custom-value.model';
import { TagCustomValueService } from 'app/entities/tag-custom-value/service/tag-custom-value.service';

import { TagUpdateComponent } from './tag-update.component';

describe('Component Tests', () => {
  describe('Tag Management Update Component', () => {
    let comp: TagUpdateComponent;
    let fixture: ComponentFixture<TagUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let tagService: TagService;
    let userService: UserService;
    let tagCustomTypeService: TagCustomTypeService;
    let tagCustomValueService: TagCustomValueService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TagUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TagUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TagUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      tagService = TestBed.inject(TagService);
      userService = TestBed.inject(UserService);
      tagCustomTypeService = TestBed.inject(TagCustomTypeService);
      tagCustomValueService = TestBed.inject(TagCustomValueService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const tag: ITag = { id: 456 };
        const person: IUser = { id: 'RSS' };
        tag.person = person;

        const userCollection: IUser[] = [{ id: 'Investment' }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [person];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TagCustomType query and add missing value', () => {
        const tag: ITag = { id: 456 };
        const customType: ITagCustomType = { id: 16909 };
        tag.customType = customType;

        const tagCustomTypeCollection: ITagCustomType[] = [{ id: 3631 }];
        spyOn(tagCustomTypeService, 'query').and.returnValue(of(new HttpResponse({ body: tagCustomTypeCollection })));
        const additionalTagCustomTypes = [customType];
        const expectedCollection: ITagCustomType[] = [...additionalTagCustomTypes, ...tagCustomTypeCollection];
        spyOn(tagCustomTypeService, 'addTagCustomTypeToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        expect(tagCustomTypeService.query).toHaveBeenCalled();
        expect(tagCustomTypeService.addTagCustomTypeToCollectionIfMissing).toHaveBeenCalledWith(
          tagCustomTypeCollection,
          ...additionalTagCustomTypes
        );
        expect(comp.tagCustomTypesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TagCustomValue query and add missing value', () => {
        const tag: ITag = { id: 456 };
        const customValue: ITagCustomValue = { id: 1939 };
        tag.customValue = customValue;

        const tagCustomValueCollection: ITagCustomValue[] = [{ id: 68095 }];
        spyOn(tagCustomValueService, 'query').and.returnValue(of(new HttpResponse({ body: tagCustomValueCollection })));
        const additionalTagCustomValues = [customValue];
        const expectedCollection: ITagCustomValue[] = [...additionalTagCustomValues, ...tagCustomValueCollection];
        spyOn(tagCustomValueService, 'addTagCustomValueToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        expect(tagCustomValueService.query).toHaveBeenCalled();
        expect(tagCustomValueService.addTagCustomValueToCollectionIfMissing).toHaveBeenCalledWith(
          tagCustomValueCollection,
          ...additionalTagCustomValues
        );
        expect(comp.tagCustomValuesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const tag: ITag = { id: 456 };
        const person: IUser = { id: 'Jordanien initiatives SSL' };
        tag.person = person;
        const customType: ITagCustomType = { id: 55844 };
        tag.customType = customType;
        const customValue: ITagCustomValue = { id: 97013 };
        tag.customValue = customValue;

        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(tag));
        expect(comp.usersSharedCollection).toContain(person);
        expect(comp.tagCustomTypesSharedCollection).toContain(customType);
        expect(comp.tagCustomValuesSharedCollection).toContain(customValue);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tag = { id: 123 };
        spyOn(tagService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tag }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(tagService.update).toHaveBeenCalledWith(tag);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tag = new Tag();
        spyOn(tagService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tag }));
        saveSubject.complete();

        // THEN
        expect(tagService.create).toHaveBeenCalledWith(tag);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tag = { id: 123 };
        spyOn(tagService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tag });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(tagService.update).toHaveBeenCalledWith(tag);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 'ABC' };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTagCustomTypeById', () => {
        it('Should return tracked TagCustomType primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTagCustomTypeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTagCustomValueById', () => {
        it('Should return tracked TagCustomValue primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTagCustomValueById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
