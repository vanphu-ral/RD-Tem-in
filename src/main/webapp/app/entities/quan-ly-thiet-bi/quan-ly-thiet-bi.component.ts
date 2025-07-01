import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { AccountService } from 'app/core/auth/account.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

@Component({
  selector: 'jhi-quan-ly-thiet-bi',
  templateUrl: './quan-ly-thiet-bi.component.html',
  styleUrls: ['./quan-ly-thiet-bi.component.scss'],
  standalone: false,
})
export class QuanLyThietBiComponent implements OnInit {
  listOfGroupMachineURL = this.applicationConfigService.getEndpointFor('api/scan-group-machines');
  listOfMachineURL = this.applicationConfigService.getEndpointFor('api/nhom-thiet-bi/get');
  listOfMachineAddURL = this.applicationConfigService.getEndpointFor('api/nhom-thiet-bi');
  addNewMachineURL = this.applicationConfigService.getEndpointFor('api/nhom-thiet-bi/update');
  getListOfMachineURL = this.applicationConfigService.getEndpointFor('api/nhom-thiet-bi');
  addNewMachineListURL = this.applicationConfigService.getEndpointFor('api/nhom-thiet-bi');
  predicate!: string;
  ascending!: boolean;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  page2?: number;
  page3?: number;
  page4?: number;
  page5?: number;

  totalItems1 = 0;
  pageSize1 = 10;
  pageIndex1 = 0;
  totalItems2 = 0;
  pageSize2 = 10;
  pageIndex2 = 0;
  totalItems3 = 0;
  pageSize3 = 10;
  pageIndex3 = 0;

  popupThemMoiNhomThietBi = false;
  popupNhomThietBi = false;
  popupKhaiBaoThietBi = false;
  popupThemMoiThietBi = false;
  popupChinhSuaThietBi = false;
  popupConfirmSave = false;
  popupConfirmSave2 = false;
  popupConfirmSave3 = false;
  popupConfirmSave4 = false;
  popupConfirmSave5 = false;

  popupConfirmDelete = false;

  // formSearch = this.formBuilder.group({
  //   groupName: '',
  //   createedAt: '',
  //   updatedAt: '',
  //   username: '',
  //   groupStatus: '',
  // });
  // acount
  account: any;
  currentDate: Date = new Date();
  @Input() groupId = 0;
  @Input() groupName = '';
  @Input() createAt = this.currentDate;
  @Input() updatedAt = this.currentDate;
  @Input() userName = '';
  @Input() groupStatus = 0;
  @Input() machineId = '';
  @Input() machineName = '';
  @Input() id = '';

  @Input() statusMachine = '';
  @Input() loaiThietBi = '';
  @Input() maThietBi = '';
  @Input() dayChuyen = '';
  @Input() itemPerPage = 10;
  @Input() itemPerPageThietBiAdd = 10;
  @Input() itemPerPageThietBi = 10;
  @Input() itemPerPageThietBiKhaiBao = 10;

  @Input() statusName = '';
  //list goi y
  listUsername: any[] = [];
  listOfNameGroupMachine: any[] = [];
  // quản lý thiết bị
  listOfGroupMachine: any[] = [];
  listOfGroupMachineAdd: any[] = [];
  groupMachine: any;
  // Thiet bi
  listOfMachines: any[] = [];
  listOfMachineAdd: any[] = [];
  listOfMachineAddGoc: any[] = [];
  machines: any;
  selectedMachines: any[] = [];
  machinesList: any;
  machinesList2: any[] = [];
  // sharedSelectedMachines: any[] = []
  // listOfMachinesInGroup: any[] = []

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected formBuilder: UntypedFormBuilder,
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,

    protected accountService: AccountService,
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    // this.isLoading = true;
    // const pageToLoad: number = page ?? this.page ?? 1;
    // this.lenhSanXuatService
    //   .query({
    //     page: pageToLoad - 1,
    //     size: this.itemsPerPage,
    //   })
    //   .subscribe({
    //     next: (res: HttpResponse<ILenhSanXuat[]>) => {
    //       this.isLoading = false;
    //       this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
    //     },
    //     error: () => {
    //       this.isLoading = false;
    //       this.onError();
    //     },
    //   });
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      this.account = account;
      // console.log('acc', this.account);
    });
    this.http.get<any>(this.listOfGroupMachineURL).subscribe(res => {
      this.listOfGroupMachine = res;
      this.listOfNameGroupMachine = res;
      for (let i = 0; i < this.listOfGroupMachine.length; i++) {
        this.listOfGroupMachine[i].statusName = this.listOfGroupMachine[i].groupStatus === 1 ? 'Active' : 'Deactive';
      }
      const map = new Map();
      this.listOfGroupMachine.map(s => map.set(s.username, { Name: s.username }));
      this.listUsername = Array.from(map.values());
      // console.log('TB', this.listUsername);
    });

    // this.listOfMachinesInGroup = this.sharedSelectedMachines
  }
  // tim kiem function
  search(): void {
    this.listOfGroupMachine = this.listOfNameGroupMachine.filter(
      item => item.groupName.includes(this.groupName) && item.username.includes(this.userName) && item.statusName.includes(this.statusName),
    );
    console.log('danh sach tim kiem', this.listOfGroupMachine);
  }
  onPageChange1(event: PageEvent): void {
    this.pageIndex1 = event.pageIndex;
    this.pageSize1 = event.pageSize;
    this.loadPage();
  }
  onPageChange2(event: PageEvent): void {
    this.pageIndex2 = event.pageIndex;
    this.pageSize2 = event.pageSize;
    this.loadPage();
  }
  onPageChange3(event: PageEvent): void {
    this.pageIndex3 = event.pageIndex;
    this.pageSize3 = event.pageSize;
    this.loadPage();
  }
  searchMachineName(): void {
    console.log('Danh sách trước khi tìm kiếm', this.listOfMachineAdd);
    console.log('Tìm kiếm với machineName:', this.machineName);
    this.listOfMachineAdd = this.listOfMachineAddGoc.filter(item2 =>
      item2.maThietBi?.toLowerCase().includes(this.machineName.toLowerCase()),
    );
    console.log('Sau khi tìm kiếm:', this.listOfMachineAdd);

    // this.listOfMachineAdd = this.listOfMachineAdd.filter(
    //   item2 => (item2.machineName && item2.machineName.includes(this.machineName)) &&
    //     (item2.id && item2.id.includes(this.id))
    // );
  }
  //Cập nhật trạng thái khi thêm mới nhóm thiết bị
  updateStatus(): void {
    this.groupStatus = this.statusName === 'Active' ? 1 : 0;
  }

  openPopupThemMoiNhomThietBi(): void {
    this.popupThemMoiNhomThietBi = true;
    this.listOfMachineAdd = [];
    this.listOfMachineAddGoc = [];
    this.selectedMachines = [];
    this.listOfMachines = [];
    this.http.get<any>(this.listOfMachineAddURL).subscribe(res => {
      this.listOfMachineAdd = res;
      this.listOfMachineAddGoc = res;
      console.log('machine:', res);
    });
    // this.http.post<any>(this.listOfGroupMachineURL, this.listOfGroupMachineAdd).subscribe(() => {
    //   console.log('them moi nhom thiet bi', this.listOfGroupMachineAdd);
    // });

    // this.http.post<any>(this.listOfGroupMachineURL, this.listOfGroupMachineAdd).subscribe(() => {
    //   // console.log("them moi nhom thiet bi", this.listOfGroupMachineAdd)
    // })
  }

  closePopupThemMoiNhomThietBi(): void {
    this.popupThemMoiNhomThietBi = false;
  }

  openPopupNhomThietBi(groupId: any): void {
    this.groupId = groupId;
    this.popupNhomThietBi = true;
    //hàm trả về giá trị index của phần tử chứa thông tin group id truyền vào
    const index = this.listOfGroupMachine.findIndex(item => item.groupId === groupId);
    console.log('index', index);

    // trả về thông tin phần tử tại vị trí  index
    this.groupMachine = this.listOfGroupMachine[index];
    console.log('nhom machine:', this.groupMachine);

    this.http.get<any>(`${this.listOfMachineURL}/${groupId as string}`).subscribe(res => {
      this.listOfMachines = res;
      // console.log('machine', this.listOfMachines);
    });
  }

  closePopupNhomThietBi(): void {
    this.popupNhomThietBi = false;
  }

  openPopupKhaiBaoThietBi(): void {
    this.listOfMachineAdd = [];
    this.listOfMachineAddGoc = [];
    this.selectedMachines = [];
    this.popupKhaiBaoThietBi = true;
    this.http.get<any>(this.listOfMachineAddURL).subscribe(res => {
      this.listOfMachineAdd = res;
      this.listOfMachineAddGoc = res;
      setTimeout(() => {
        for (let i = 0; i < this.listOfMachineAdd.length; i++) {
          this.listOfMachineAdd[i].checked = false;
          for (let j = 0; j < this.listOfMachines.length; j++) {
            if (this.listOfMachines[j].maThietBi === this.listOfMachineAdd[i].maThietBi) {
              this.listOfMachineAdd[i].checked = true;
              this.selectedMachines.push(this.listOfMachineAdd[i]);
            }
          }
        }
      }, 200);
      console.log('machine:', res);
    });
  }

  closePopupKhaiBaoThietBi(): void {
    this.popupKhaiBaoThietBi = false;
  }

  openPopupConfirmSave(): void {
    this.popupConfirmSave = true;
  }

  closePopupConfirmSave(): void {
    this.popupConfirmSave = false;
  }

  openPopupConfirmSave2(): void {
    this.popupConfirmSave2 = true;
  }

  closePopupConfirmSave2(): void {
    this.popupConfirmSave2 = false;
  }

  openPopupConfirmSave3(): void {
    this.popupConfirmSave3 = true;
  }

  closePopupConfirmSave3(): void {
    this.popupConfirmSave3 = false;
  }

  openPopupConfirmSave4(): void {
    this.popupConfirmSave4 = true;
  }

  closePopupConfirmSave4(): void {
    this.popupConfirmSave4 = false;
  }

  openPopupConfirmSave5(): void {
    this.popupConfirmSave5 = true;
  }

  closePopupConfirmSave5(): void {
    this.popupConfirmSave5 = false;
  }

  openPopupConfirmDelete(): void {
    this.popupConfirmDelete = true;
  }

  closePopupConfirmDelete(): void {
    this.popupConfirmDelete = false;
  }

  updateSelectedMachines(machine: any, event: any): void {
    if (event.target.checked) {
      this.selectedMachines.push(machine);
    } else {
      this.selectedMachines = this.selectedMachines.filter(m => m.id !== machine.id);
    }
    console.log('select', this.selectedMachines);
  }

  saveSelectedMachines(): void {
    this.listOfMachines = this.selectedMachines.map(machine => ({
      groupId: this.groupId,
      id: machine.id,
      maThietBi: machine.maThietBi,
    }));
    this.closePopupKhaiBaoThietBi();
    this.closePopupConfirmSave3();
    console.log('list machine', this.listOfMachines);
  }

  isMachineSelected(machine: any): any {
    // console.log('machine', machine.machineId)
    return this.selectedMachines.some(m => m.id === machine.id);
  }
  // cập nhật thông tin nhóm thiết bị
  updateGroupMachine(): void {
    console.log('tesst', this.groupMachine);
    const currentTime = formatDate(Date.now(), 'yyyy-MM-dd HH:mm:ss', 'en-US');
    this.groupMachine.updateAt = currentTime;
    this.groupMachine.username = this.account.login;
    //api cập nhật thông tin nhóm thiết bị
    this.http.put<any>(this.listOfGroupMachineURL, this.groupMachine).subscribe(() => {
      this.showSuccessModalUpdateGroupMachine();
    });
  }

  addNewGroupMachine(): void {
    const dataToSend = {
      groupName: this.groupName,
      createAt: this.currentDate,
      updateAt: this.currentDate,
      username: this.account.login,
      groupStatus: this.groupStatus,
      thietBis: this.listOfMachines,
    };
    this.http.post<any>(this.listOfGroupMachineURL, dataToSend).subscribe(res => {
      console.log('them moi nhom thiet bi', res);
      this.groupId = res;
    });
    this.popupConfirmSave2 = false;
  }

  addNewMachineListGroup(): void {
    this.http.post<any>(this.addNewMachineURL, this.listOfMachines).subscribe(() => {
      console.log('them moi thiet bi danh sach', this.listOfMachines);
    });
    window.location.reload();
  }

  updateMachineListGroup(): void {
    this.http.put<any>(this.addNewMachineURL, this.machinesList).subscribe(() => {
      console.log('them moi thiet bi danh sach', this.machinesList);
    });
  }

  addNewMachine(): void {
    const dataMachine = [
      {
        loaiThietBi: this.loaiThietBi,
        maThietBi: this.maThietBi,
        dayChuyen: this.dayChuyen,
      },
    ];
    console.log('add new machine', dataMachine);
    this.http.post<any>(this.addNewMachineListURL, dataMachine).subscribe(res => {
      this.listOfMachineAdd.push(res);
      this.listOfMachineAddGoc.push(res);
      console.log('them moi thiet bi', this.listOfMachineAdd);
    });
    window.location.reload();
  }

  updateMachine(): void {
    const currentTime = formatDate(Date.now(), 'yyyy-MM-dd HH:mm:ss', 'en-US');
    this.machines.updateAt = currentTime;
    this.machines.username = this.account.login;
    this.http.put<any>(this.addNewMachineURL, this.machines).subscribe(() => {
      // alert('update thành công');
      this.showSuccessModalUpdateMachine();
    });
  }

  openPopupAddNewMachine(): void {
    this.popupThemMoiThietBi = true;
  }

  closePopupAddNewMachine(): void {
    this.popupThemMoiThietBi = false;
  }

  openPopupChinhSuaThietBi(id: any): void {
    this.popupChinhSuaThietBi = true;

    const index = this.listOfMachineAdd.findIndex(item => item.id === id);
    console.log('index', index);

    this.machinesList = this.listOfMachineAdd[index];
    console.log('ten thiet bi', this.machinesList);

    this.http.get<any>(`${this.listOfMachineAddURL}/${id as string}`).subscribe(res => {
      this.listOfMachineAdd = res;
      this.listOfMachineAddGoc = res;
      console.log('machine by id', this.listOfMachineAdd);
    });
  }

  closePopupChinhSuaThietBi(): void {
    this.popupChinhSuaThietBi = false;
  }

  updateThietBi(): void {
    this.machinesList2.push(this.machinesList);
    console.log('testt', this.machinesList2);
    this.http.post<any>(this.addNewMachineListURL, this.machinesList2).subscribe(() => {
      // alert('update thành công');
      this.showSuccessModalUpdateMachine();
    });
  }

  showSuccessModalUpdateGroupMachine(): void {
    const modal = document.getElementById('successModalUpdateGroupMachine');
    const button = document.getElementsByClassName('closeUpdateGroupMachine')[0] as HTMLElement;
    if (modal) {
      modal.style.display = 'block';

      button.onclick = () => {
        modal.style.display = 'none';
      };

      window.onclick = event => {
        if (event.target === modal) {
          modal.style.display = 'none';
        }
      };
    }
  }

  showSuccessModalUpdateMachine(): void {
    const modal = document.getElementById('successModalUpdateMachine');
    const button = document.getElementsByClassName('closeUpdateMachine')[0] as HTMLElement;
    if (modal) {
      modal.style.display = 'block';

      button.onclick = () => {
        modal.style.display = 'none';
      };

      window.onclick = event => {
        if (event.target === modal) {
          modal.style.display = 'none';
        }
      };
    }
  }
}
