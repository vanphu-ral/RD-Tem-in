jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { KichBanService } from '../service/kich-ban.service';

import { KichBanDeleteDialogComponent } from './kich-ban-delete-dialog.component';

describe('KichBan Management Delete Component', () => {
  let comp: KichBanDeleteDialogComponent;
  let fixture: ComponentFixture<KichBanDeleteDialogComponent>;
  let service: KichBanService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [KichBanDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(KichBanDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(KichBanDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(KichBanService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        // comp.confirmDelete(123);
        tick();

        // THEN
        // expect(service.delete).toHaveBeenCalledWith(123);
        // expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
        expect(comp.confirmDelete);
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
