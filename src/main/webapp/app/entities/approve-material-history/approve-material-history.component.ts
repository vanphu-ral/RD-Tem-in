import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { ChangeDetectionStrategy, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatSort } from '@angular/material/sort';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Subscription, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { SelectionModel } from '@angular/cdk/collections';
import {
  inventory_update_requests,
  inventory_update_requests_history_detail,
  ListMaterialService,
} from '../list-material/services/list-material.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { DialogContentExampleDialogComponent, ConfirmDialogData } from '../list-material/confirm-dialog/confirm-dialog.component';
import { TimestampToDatePipe } from 'app/shared/pipes/timestamp-to-date';

export interface ColumnConfig {
  name: string;
  matColumnDef: string;
  completed: boolean;
}

export const STATUS_LABELS: Record<string, string> = {
  PENDING: 'Đang chờ duyệt',
  APPROVE: 'Đã phê duyệt',
  REJECT: 'Từ chối duyệt',
};
export interface ColumnSelectionGroup {
  name: string;
  completed: boolean;
  subtasks?: ColumnConfig[];
}
export interface FilterDialogData {
  columnName: string;
  currentValues: any[];
  selectedValues: any[];
}

@Component({
  selector: 'jhi-approve-material-update',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormBuilder],
  templateUrl: './approve-material-history.component.html',
  styleUrls: ['./approve-material-history.componennt.scss'],
  animations: [
    trigger('detailExpand', [
      state(
        'collapsed, void',
        style({
          height: '0px',
          minHeight: '0',
        }),
      ),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
      transition('expanded <=> void', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ApproveMaterialHistoryComponent implements OnInit, AfterViewInit {
  // #region Public properties

  expandedElement: inventory_update_requests | null = null;
  selection = new SelectionModel<inventory_update_requests_history_detail>(true, []);
  STATUS_LABELS = STATUS_LABELS;
  tableMaxWidth: string = '100%';
  displayedColumns: string[] = ['detail', 'requestCode', 'createdTime', 'updatedTime', 'requestedBy', 'approvedBy', 'status'];
  displayedColumnsDetails: string[] = [
    'materialId',
    'requestCode',
    'requestedBy',
    'quantity',
    'quantityChange',
    'expiredTime',
    'requestType',
    'oldLocation',
    'newLocation',
    'approvedTime',
    'requestedTime',
    'status',
  ];
  tableWidth: string = '100%';
  value = '';
  dataSource_update_manage = new MatTableDataSource<inventory_update_requests>();
  dataSoure_history_detail = new MatTableDataSource<inventory_update_requests_history_detail>();
  columnFilters: { [key: string]: string } = {};
  pageEvent: PageEvent | undefined;
  length = 0;
  pageSize = 15;
  pageIndex = 0;
  pageSizeOptions = [10, 15, 25, 50, 100];
  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  disabled = false;

  public searchTerms: { [columnDef: string]: { mode: string; value: string } } = {};
  public activeFilters: { [columnDef: string]: any[] } = {};
  public filterModes: { [columnDef: string]: string } = {};

  @ViewChild(MatPaginator, { static: false, read: MatPaginator }) paginator!: MatPaginator;
  @ViewChild('detailPaginator', { static: false }) detailPaginator!: MatPaginator;
  @ViewChild('menuTrigger') menuTrigger!: MatMenuTrigger;
  @ViewChild('sort') sort!: MatSort;
  // #region Constructor
  private tsPipe = new TimestampToDatePipe();
  private sidebarSubscription!: Subscription;
  constructor(
    private MaterialService: ListMaterialService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    // private sidebarService: any
  ) {}
  // #endregion
  public isExpansionDetailRow = (i: number, row: object): boolean => Object.prototype.hasOwnProperty.call(row, 'detailRow');

  // #endregion

  // #region Lifecycle hooks
  ngOnInit(): void {
    this.loadData();
    this.ngOnInitFilterPredicate();
    this.searchTerms = {};
    this.activeFilters = {};
    this.filterModes = {};
  }

  ngAfterViewInit(): void {
    this.dataSource_update_manage.paginator = this.paginator;
    this.dataSource_update_manage.sort = this.sort;

    const statusRank: Record<string, number> = {
      PENDING: 3,
      APPROVE: 2,
      REJECT: 1,
    };
    this.sort.active = 'status';
    this.sort.direction = 'desc';
    this.sort.sortChange.emit({ active: 'status', direction: 'desc' });
  }
  // #endregion

  // #region Public methods
  public setFilterMode(colDef: string, mode: string): void {
    this.filterModes[colDef] = mode;
    if (this.searchTerms[colDef]) {
      this.searchTerms[colDef].mode = mode;
      this.applyCombinedFilters();
    }
  }

  public applyDateFilter(colDef: string, event: MatDatepickerInputEvent<Date>): void {
    const dateValue: Date | null = event.value;
    if (dateValue) {
      const formattedDate = dateValue
        .toLocaleDateString('vi-VN', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
        })
        .toLowerCase();
      const selectedMode = this.filterModes[colDef] || 'contains';

      this.searchTerms[colDef] = {
        mode: selectedMode,
        value: formattedDate,
      };
    } else {
      this.searchTerms[colDef] = { mode: '', value: '' };
    }
    this.applyCombinedFilters();
  }

  public applyFilter(colDef: string, eventOrValue: Event | string): void {
    let rawValue: string;
    if (typeof eventOrValue === 'string') {
      rawValue = eventOrValue;
    } else {
      rawValue = (eventOrValue.target as HTMLInputElement).value;
    }
    const filterValue = rawValue.trim().toLowerCase();

    if (!this.searchTerms[colDef]) {
      this.searchTerms[colDef] = {
        mode: this.filterModes[colDef] || 'contains',
        value: filterValue,
      };
    } else {
      this.searchTerms[colDef].value = filterValue;
    }

    console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);
    this.applyCombinedFilters();
  }

  public onLoad(): void {}

  public export(): void {
    const raw = this.dataSource_update_manage.filteredData;
    const toExport = raw.map(r => ({
      requestCode: r.requestCode,
      updatedBy: r.requestedBy,
      approvedBy: r.approvedBy,
      status: r.status,
      createdTime: this.tsPipe.transform(r.createdTime),
      updatedTime: this.tsPipe.transform(r.updatedTime),
    }));
    this.MaterialService.exportExcel(toExport, 'Danh_Sach_Lich_Su_Cap_Nhat');
  }

  public exportExpandedDetails(requestElement: inventory_update_requests): void {
    if (
      this.expandedElement &&
      this.expandedElement.requestCode === requestElement.requestCode &&
      this.dataSoure_history_detail.data &&
      this.dataSoure_history_detail.data.length > 0
    ) {
      const dataSource = this.dataSoure_history_detail;
      const pageIndex = dataSource.paginator?.pageIndex ?? 0;
      const pageSize = dataSource.paginator?.pageSize ?? dataSource.data.length;
      const startIndex = pageIndex * pageSize;
      const endIndex = startIndex + pageSize;
      const currentPageData = dataSource.data.slice(startIndex, endIndex);

      const toExport = currentPageData.map(row => ({
        materialId: row.materialId,
        requestCode: row.requestCode,
        requestedBy: row.requestedBy,
        quantity: row.quantity,
        quantityChange: row.quantityChange,
        expiredTime: row.expiredTime,
        requestType: row.requestType,
        oldLocation: row.oldLocation,
        newLocation: row.newLocation,
        approvedTime: this.tsPipe.transform(row.approvedTime),
        requestedTime: this.tsPipe.transform(row.requestedTime),
        status: this.STATUS_LABELS[row.status] || row.status,
      }));

      const fileName = `ChiTietYeuCau_${requestElement.requestCode ?? 'data'}`;
      this.MaterialService.exportExcel(toExport, fileName);
    } else {
      console.warn('Không thể xuất dữ liệu chi tiết', {
        currentExpandedId: this.expandedElement?.requestCode,
        targetElementId: requestElement.id,
        detailDataCount: this.dataSoure_history_detail.data.length,
      });
    }
  }

  public toggleRowExpansion(element: inventory_update_requests): void {
    console.log('toggleRowExpansion', element);
    const isAlreadyExpanded = this.expandedElement === element;
    this.expandedElement = isAlreadyExpanded ? null : element;

    this.selection = new SelectionModel<inventory_update_requests_history_detail>(true, []);
    this.dataSoure_history_detail = new MatTableDataSource<inventory_update_requests_history_detail>([]);

    if (this.expandedElement) {
      this.MaterialService.getRequestHistoryDetailsByRequestCode(element.requestCode).subscribe(details => {
        this.dataSoure_history_detail.data = details;
        setTimeout(() => {
          this.dataSoure_history_detail.paginator = this.detailPaginator;
          this.cdr.detectChanges();
        });
      });
    }
  }

  // public fetchRequestDetails(requestId: number): void {
  //   console.log(`[MaterialUpdateRequestComponent] fetchRequestDetails called with requestId: ${requestId}`);
  //   this.MaterialService.getRequestHistoryDetailsById(requestId).subscribe({
  //     next: (details: inventory_update_requests_history_detail[]) => {
  //       console.log(`[MaterialUpdateRequestComponent] Details received for requestId ${requestId}:`, details);
  //       if (details && details.length > 0) {
  //         this.dataSoure_history_detail.data = details;
  //       } else {
  //         console.warn(`[MaterialUpdateRequestComponent] No details found for requestId ${requestId}. Displaying empty table.`);
  //         this.dataSoure_history_detail.data = [];
  //       }
  //       this.cdr.markForCheck();
  //     },
  //     error: (error: any) => {
  //       console.error(`[MaterialUpdateRequestComponent] Error fetching material changes for request ${requestId}:`, error);
  //       this.dataSoure_history_detail.data = [];
  //       this.cdr.markForCheck();
  //     },
  //     complete: () => {
  //       console.log(`[MaterialUpdateRequestComponent] Fetching details completed for requestId ${requestId}.`);
  //     },
  //   });
  // }

  // public refuseRequest(requestId: number): void {
  //   const dialogData: ConfirmDialogData = {
  //     message: 'Bạn có chắc chắn từ chối tất cả yêu cầu cập nhật này không?',
  //     confirmText: 'Từ chối',
  //     cancelText: 'Hủy',
  //   };

  //   const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
  //     width: '500px',
  //     data: dialogData,
  //     disableClose: true,
  //   });

  //   dialogRef.afterClosed().subscribe(result => {
  //     if (result === true) {
  //       console.log(`Yêu cầu ${requestId} đã bị từ chối.`);

  //       this.MaterialService.getRequestHistoryDetailsById(requestId).subscribe({
  //         next: details => {
  //           if (details && details.length > 0) {
  //             const payload = {
  //               updatedItems: details,
  //               selectedWarehouse: null,
  //               approvers: [],
  //             };
  //             const currentUser = 'USER';

  //             this.MaterialService.postRejectInventoryUpdate(requestId, payload, currentUser).subscribe({
  //               next: response => {
  //                 console.log(`Yêu cầu ${requestId} đã được từ chối thành công:`, response);
  //                 this.loadData();
  //               },
  //               error: err => {
  //                 console.error(`Lỗi khi từ chối yêu cầu ${requestId}:`, err);
  //               },
  //             });
  //           } else {
  //             console.warn(`Không tìm thấy chi tiết cho yêu cầu ${requestId} để từ chối.`);
  //           }
  //         },
  //         error: err => {
  //           console.error(`Lỗi khi lấy chi tiết yêu cầu ${requestId} để từ chối:`, err);
  //         },
  //       });
  //     } else {
  //       console.log(`Hành động từ chối yêu cầu ${requestId} đã được hủy.`);
  //     }
  //   });
  // }

  // public acceptRequest(requestId: number): void {
  //   const dialogData: ConfirmDialogData = {
  //     message: 'Bạn có chắc chắn muốn chấp thuận yêu cầu cập nhật này không?',
  //     confirmText: 'Chấp thuận',
  //     cancelText: 'Hủy',
  //   };

  //   const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
  //     width: '500px',
  //     data: dialogData,
  //     disableClose: true,
  //   });
  //   dialogRef.afterClosed().subscribe(result => {
  //     if (result === true) {
  //       console.log(`Yêu cầu ${requestId} đã được chấp thuận.`);
  //       this.MaterialService.getRequestHistoryDetailsById(requestId).subscribe({
  //         next: details => {
  //           if (details && details.length > 0) {
  //             const payload = {
  //               updatedItems: details,
  //               selectedWarehouse: null,
  //               approvers: [],
  //             };
  //             const currentUser = 'USER';
  //             this.MaterialService.postApproveInventoryUpdate(requestId, payload, currentUser).subscribe({
  //               next: response => {
  //                 console.log(`Yêu cầu ${requestId} đã được chấp thuận thành công:`, response);

  //                 this.loadData();
  //               },
  //               error: err => {
  //                 console.error(`Lỗi khi chấp thuận yêu cầu ${requestId}:`, err);
  //               },
  //             });
  //           } else {
  //             console.warn(`Không tìm thấy chi tiết cho yêu cầu ${requestId} để chấp thuận.`);
  //           }
  //         },
  //         error: err => {
  //           console.error(`Lỗi khi lấy chi tiết yêu cầu ${requestId} để chấp thuận:`, err);
  //         },
  //       });
  //     } else {
  //       console.log(`Hành động chấp thuận yêu cầu ${requestId} đã được hủy.`);
  //     }
  //   });
  // }

  public handleDetailStatusChange(row: inventory_update_requests_history_detail, isChecked: boolean): void {
    const newStatus = isChecked ? 'APPROVE' : 'REJECT';

    const currentData = this.dataSoure_history_detail.data;
    const rowIndex = currentData.findIndex(item => item.id === row.id);

    if (rowIndex > -1) {
      const originalItemInDataSource = currentData[rowIndex];
      const updatedItem = { ...originalItemInDataSource, status: newStatus };
      const newDataSourceData = [...currentData];
      newDataSourceData[rowIndex] = updatedItem;
      this.dataSoure_history_detail.data = newDataSourceData;

      const itemsInSelectionWithSameId = this.selection.selected.filter(selectedItem => selectedItem.id === row.id);
      if (itemsInSelectionWithSameId.length > 0) {
        this.selection.deselect(...itemsInSelectionWithSameId);
      }

      if (isChecked) {
        this.selection.select(updatedItem);
      }
      console.log(`Đã cập nhật status cho item ID ${updatedItem.id}: ${updatedItem.status}`);
    } else {
      console.warn('Không tìm thấy dòng trong dataSource để cập nhật status:', row);
    }
    this.cdr.markForCheck();
  }

  public isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSoure_history_detail.data.length;
    return numSelected === numRows;
  }

  public toggleAllRows(): void {
    const isCurrentlyAllSelected = this.isAllSelected();
    let newDataSourceData: inventory_update_requests_history_detail[];

    if (isCurrentlyAllSelected) {
      newDataSourceData = this.dataSoure_history_detail.data.map(dRow => ({
        ...dRow,
        status: 'REJECT',
      }));
      this.dataSoure_history_detail.data = newDataSourceData;
      this.selection.clear();
      console.log('Đã bỏ chọn tất cả và cập nhật status thành "Từ chối"');
    } else {
      newDataSourceData = this.dataSoure_history_detail.data.map(dRow => ({
        ...dRow,
        status: 'APPROVE',
      }));
      this.dataSoure_history_detail.data = newDataSourceData;
      this.selection.select(...this.dataSoure_history_detail.data);
      console.log('Đã chọn tất cả và cập nhật status thành "Chấp thuận"');
    }
    this.cdr.markForCheck();
  }

  public checkboxLabel(row?: inventory_update_requests_history_detail): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row`;
  }

  public handlePageEvent(e: PageEvent): void {
    this.pageEvent = e;
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
  }

  public setPageSizeOptions(setPageSizeOptionsInput: string): void {
    if (setPageSizeOptionsInput) {
      this.pageSizeOptions = setPageSizeOptionsInput.split(',').map(str => +str);
    }
  }

  public openMenuManually(): void {
    this.menuTrigger.openMenu();
  }

  public closeMenuManually(): void {
    this.menuTrigger.closeMenu();
  }
  // #endregion
  // #region Filter Predicate
  ngOnInitFilterPredicate(): void {
    this.dataSource_update_manage.filterPredicate = (data: inventory_update_requests, filter: string): boolean => {
      const combinedFilters = JSON.parse(filter) as {
        textFilters: { [columnDef: string]: { mode: string; value: string } };
        dialogFilters: { [columnDef: string]: any[] };
      };

      if (combinedFilters.textFilters) {
        for (const colDef in combinedFilters.textFilters) {
          if (!Object.prototype.hasOwnProperty.call(combinedFilters.textFilters, colDef)) {
            continue;
          }

          const filterObj = combinedFilters.textFilters[colDef];
          const searchMode = filterObj.mode;
          const searchTerm = filterObj.value.trim().toLowerCase();
          if (searchTerm === '') {
            continue;
          }

          const rawValue = (data as any)[colDef];
          if (rawValue == null) {
            return false;
          }

          let cellValue = '';
          if (colDef === 'status') {
            // chuyển code sang label tiếng Việt
            cellValue = STATUS_LABELS[rawValue] || rawValue.toLowerCase();
            const codeValue = rawValue.toLowerCase();
            if (
              (searchMode === 'contains' && !cellValue.toLowerCase().includes(searchTerm) && !codeValue.includes(searchTerm)) ||
              (searchMode === 'not_contains' && (cellValue.toLowerCase().includes(searchTerm) || codeValue.includes(searchTerm))) ||
              (searchMode === 'equals' && cellValue.toLowerCase() !== searchTerm && codeValue !== searchTerm) ||
              (searchMode === 'not_equals' && (cellValue.toLowerCase() === searchTerm || codeValue === searchTerm))
            ) {
              return false;
            }
            continue;
          }

          if (['createdTime', 'updatedTime'].includes(colDef)) {
            let dateObj: Date;

            if (typeof rawValue === 'number') {
              dateObj = new Date(rawValue * 1000);
            } else if (typeof rawValue === 'string') {
              const trimmed = rawValue.trim();
              const digitsOnly = /^\d+$/;
              if (digitsOnly.test(trimmed)) {
                dateObj = new Date(Number(trimmed) * 1000);
              } else {
                const bracketIndex = trimmed.indexOf('[');
                const processed = bracketIndex > -1 ? trimmed.substring(0, bracketIndex) : trimmed;
                dateObj = new Date(processed);
              }
            } else {
              dateObj = new Date(rawValue);
            }

            if (isNaN(dateObj.getTime())) {
              return false;
            }

            cellValue = dateObj
              .toLocaleDateString('vi-VN', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
              })
              .toLowerCase();
          } else {
            cellValue = String(rawValue).trim().toLowerCase();
          }

          if (searchMode === 'contains') {
            if (!cellValue.includes(searchTerm)) {
              return false;
            }
          } else if (searchMode === 'not_contains') {
            if (cellValue.includes(searchTerm)) {
              return false;
            }
          } else if (searchMode === 'equals') {
            if (cellValue !== searchTerm) {
              return false;
            }
          } else if (searchMode === 'not_equals') {
            if (cellValue === searchTerm) {
              return false;
            }
          } else {
            if (!cellValue.includes(searchTerm)) {
              return false;
            }
          }
        }
      }

      if (combinedFilters.dialogFilters) {
        for (const colDef in combinedFilters.dialogFilters) {
          if (!Object.prototype.hasOwnProperty.call(combinedFilters.dialogFilters, colDef)) {
            continue;
          }
          const selectedValuesFromDialog = combinedFilters.dialogFilters[colDef];
          if (!selectedValuesFromDialog || selectedValuesFromDialog.length === 0) {
            continue;
          }

          const normalizedSelectedValues = selectedValuesFromDialog.map(val => String(val).trim().toLowerCase());
          const cellValue = (data as any)[colDef]
            ? String((data as any)[colDef])
                .trim()
                .toLowerCase()
            : '';
          if (!normalizedSelectedValues.includes(cellValue)) {
            return false;
          }
        }
      }
      return true;
    };
    this.dataSource_update_manage.paginator = this.paginator;
  }
  // #endregion
  // #region Private methods
  private applyCombinedFilters(): void {
    const combinedFilterData = {
      textFilters: this.searchTerms,
      dialogFilters: this.activeFilters,
      timestamp: new Date().getTime(),
    };
    console.log('[applyCombinedFilters] - combinedFilterData:', combinedFilterData);
    this.dataSource_update_manage.filter = JSON.stringify(combinedFilterData);

    if (this.dataSource_update_manage.paginator) {
      this.dataSource_update_manage.paginator.firstPage();
    }
  }

  private loadData(): void {
    this.MaterialService.getDataUpdateRequest().subscribe(items => {
      const filtered = items.filter(item => ['APPROVE', 'REJECT'].includes(item.status));
      const statusRank: Record<string, number> = { PENDING: 3, APPROVE: 2, REJECT: 1 };
      filtered.sort((a, b) => {
        const sa = statusRank[a.status] || 0;
        const sb = statusRank[b.status] || 0;
        if (sa !== sb) {
          return sb - sa;
        }
        return new Date(b.createdTime).getTime() - new Date(a.createdTime).getTime();
      });
      this.dataSource_update_manage.data = filtered;
      setTimeout(() => {
        this.sort.active = 'status';
        this.sort.direction = 'desc';
        this.sort.sortChange.emit({ active: 'status', direction: 'desc' });
      });
    });
  }

  // #endregion
}
