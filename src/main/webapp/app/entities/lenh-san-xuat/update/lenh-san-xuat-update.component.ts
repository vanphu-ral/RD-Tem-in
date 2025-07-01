import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from './../../../config/input.constants';
import { IChiTietLenhSanXuat } from './../../chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.model';
import { Component, Input, OnInit } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ILenhSanXuat, LenhSanXuat } from '../lenh-san-xuat.model';
import { LenhSanXuatService } from '../service/lenh-san-xuat.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'jhi-lenh-san-xuat-update',
  templateUrl: './lenh-san-xuat-update.component.html',
  styleUrls: ['./lenh-san-xuat-update.component.css'],
  standalone: false,
})
export class LenhSanXuatUpdateComponent implements OnInit {
  resourceUrl = this.applicationConfigService.getEndpointFor('/api/chi-tiet-lenh-san-xuat');
  resourceUrl1 = this.applicationConfigService.getEndpointFor('/api/chi-tiet-lenh-san-xuat/update');
  chiTietLenhSanXuats: IChiTietLenhSanXuat[] = [];

  @Input() storageUnit = '';
  @Input() itemPerPage = 10;
  page?: number;

  isSaving = false;
  predicate!: string;
  ascending!: boolean;
  tongSoLuong = 0;
  changeStatus: {
    id: number;
    totalQuantity: string;
    timeUpdate: dayjs.Dayjs;
    trangThai: string;
  } = { id: 0, totalQuantity: '', timeUpdate: dayjs().startOf('second'), trangThai: '' };

  editForm = this.fb.group({
    id: [],
    maLenhSanXuat: [null, [Validators.required]],
    sapCode: [],
    sapName: [],
    workOrderCode: [],
    version: [],
    storageCode: [],
    totalQuantity: [],
    createBy: [],
    entryTime: [],
    timeUpdate: [],
    trangThai: [],
    comment: [],
    groupName: [],
    comment2: [],
  });
  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  @Input() reelID = '';
  account: Account | null = null;
  constructor(
    protected lenhSanXuatService: LenhSanXuatService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder,
    protected applicationConfigService: ApplicationConfigService,
    protected http: HttpClient,
    protected formBuilder: UntypedFormBuilder,
    protected accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      this.account = account;
    });
    this.activatedRoute.data.subscribe(({ lenhSanXuat }) => {
      const today = dayjs().startOf('second');
      // this.editForm.patchValue({ timeUpdate: today });
      // set timeupdate
      lenhSanXuat.timeUpdate = today;
      // gán thông tin xác định vào changeStatus
      this.changeStatus.id = lenhSanXuat.id;
      this.changeStatus.totalQuantity = lenhSanXuat.totalQuantity;
      this.http.get<any>(`${this.resourceUrl}/${lenhSanXuat.id as number}`).subscribe(res => {
        this.chiTietLenhSanXuats = res;
        // this.itemPerPage = this.chiTietLenhSanXuats.length;
        this.itemPerPage = this.chiTietLenhSanXuats.length;
      });
      this.updateForm(lenhSanXuat);
    });
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  trackId(_index: number, item: IChiTietLenhSanXuat): number {
    return item.id!;
  }

  previousState(): void {
    window.history.back();
  }

  guiPheDuyet(): void {
    this.editForm.patchValue({
      trangThai: 'Chờ duyệt',
    });
    const lenhSanXuat = this.createFromForm();
    this.lenhSanXuatService.update(lenhSanXuat).subscribe(() => {
      // console.log(this.editForm)
      this.http.put<any>(`${this.resourceUrl1}/${this.editForm.get(['id'])!.value as number}`, this.chiTietLenhSanXuats).subscribe();
      alert('Gửi phê duyệt thành công');
      this.previousState();
    });
  }

  boPhanSanXuatHuy(): void {
    this.editForm.patchValue({
      trangThai: 'Sản xuất hủy',
    });
    const lenhSanXuat = this.createFromForm();
    this.lenhSanXuatService.update(lenhSanXuat).subscribe(() => {
      this.http.put<any>(`${this.resourceUrl1}/${this.editForm.get(['id'])!.value as number}`, this.chiTietLenhSanXuats).subscribe();
      alert('Huỷ thành công');
      this.previousState();
    });
  }

  save(): void {
    this.isSaving = true;
    const lenhSanXuat = this.createFromForm();
    if (lenhSanXuat.id !== undefined) {
      this.subscribeToSaveResponse(this.lenhSanXuatService.update(lenhSanXuat));
      this.http.put<any>(`${this.resourceUrl1}/${this.editForm.get(['id'])!.value as number}`, this.chiTietLenhSanXuats).subscribe(() => {
        alert('Lưu thành công');
      });
    } else {
      this.subscribeToSaveResponse(this.lenhSanXuatService.create(lenhSanXuat));
    }
  }

  subscribeToSaveResponse(result: Observable<HttpResponse<ILenhSanXuat>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  onSaveSuccess(): void {
    this.previousState();
  }

  onSaveError(): void {
    // Api for inheritance.
  }

  onSaveFinalize(): void {
    this.isSaving = false;
  }
  // bắt sự kiện thay đổi số lượng
  changeQuantity(): void {
    // cộng lại số lượng tổng
    this.tongSoLuong = 0;
    for (let i = 0; i < this.chiTietLenhSanXuats.length; i++) {
      if (this.chiTietLenhSanXuats[i].trangThai === 'Active') {
        const result = this.chiTietLenhSanXuats[i].initialQuantity;
        if (result) {
          this.tongSoLuong += Number(result);
        }
      }
    }
    this.editForm.patchValue({
      totalQuantity: this.tongSoLuong,
    });
  }
  // cập nhật storageUnit tất cả danh sách
  changeAllStorageUnit(): void {
    for (let i = 0; i < this.chiTietLenhSanXuats.length; i++) {
      this.chiTietLenhSanXuats[i].storageUnit = this.storageUnit;
    }
  }
  updateForm(lenhSanXuat: ILenhSanXuat): void {
    this.editForm.patchValue({
      id: lenhSanXuat.id,
      maLenhSanXuat: lenhSanXuat.maLenhSanXuat,
      sapCode: lenhSanXuat.sapCode,
      sapName: lenhSanXuat.sapName,
      workOrderCode: lenhSanXuat.workOrderCode,
      version: lenhSanXuat.version,
      storageCode: lenhSanXuat.storageCode,
      totalQuantity: lenhSanXuat.totalQuantity,
      createBy: lenhSanXuat.createBy,
      entryTime: lenhSanXuat.entryTime ? lenhSanXuat.entryTime.format(DATE_TIME_FORMAT) : null,
      timeUpdate: lenhSanXuat.timeUpdate ? lenhSanXuat.timeUpdate.format(DATE_TIME_FORMAT) : null,
      trangThai: lenhSanXuat.trangThai,
      comment: lenhSanXuat.comment,
      groupName: lenhSanXuat.groupName,
      comment2: lenhSanXuat.comment2,
    });
  }

  createFromForm(): ILenhSanXuat {
    return {
      ...new LenhSanXuat(),
      id: this.editForm.get(['id'])!.value,
      maLenhSanXuat: this.editForm.get(['maLenhSanXuat'])!.value,
      sapCode: this.editForm.get(['sapCode'])!.value,
      sapName: this.editForm.get(['sapName'])!.value,
      workOrderCode: this.editForm.get(['workOrderCode'])!.value,
      version: this.editForm.get(['version'])!.value,
      storageCode: this.editForm.get(['storageCode'])!.value,
      totalQuantity: this.editForm.get(['totalQuantity'])!.value,
      createBy: this.account?.login,
      entryTime: this.editForm.get(['entryTime'])!.value ? dayjs(this.editForm.get(['entryTime'])!.value, DATE_TIME_FORMAT) : undefined,
      timeUpdate: this.editForm.get(['timeUpdate'])!.value ? dayjs(this.editForm.get(['timeUpdate'])!.value, DATE_TIME_FORMAT) : undefined,
      trangThai: this.editForm.get(['trangThai'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      comment2: this.editForm.get(['comment2'])!.value,
      groupName: this.editForm.get(['groupName'])!.value,
    };
  }
}
