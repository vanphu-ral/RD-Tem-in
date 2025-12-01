import { IChiTietLenhSanXuat } from "app/entities/chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.model";
import dayjs from "dayjs/esm";

export interface ILenhSanXuat {
  id?: number;
  maLenhSanXuat?: string;
  sapCode?: string | null;
  sapName?: string | null;
  workOrderCode?: string | null;
  version?: string | null;
  storageCode?: string | null;
  totalQuantity?: number | null;
  createBy?: string | null;
  entryTime?: dayjs.Dayjs | null;
  timeUpdate?: dayjs.Dayjs | null;
  trangThai?: string | null;
  comment?: string | null;
  groupName?: string | null;
  comment2?: string | null;
  approverBy?: string | null;
  branch?: string | null;
  productType?: string | null;
  deletedAt?: dayjs.Dayjs | null;
  deletedBy?: string | null;
  destinationWarehouse?: number | null;
  chiTietLenhSanXuats?: IChiTietLenhSanXuat[] | null;
}

export class LenhSanXuat implements ILenhSanXuat {
  constructor(
    public id?: number,
    public maLenhSanXuat?: string,
    public sapCode?: string | null,
    public sapName?: string | null,
    public workOrderCode?: string | null,
    public version?: string | null,
    public storageCode?: string | null,
    public totalQuantity?: number | null,
    public createBy?: string | null,
    public entryTime?: dayjs.Dayjs | null,
    public timeUpdate?: dayjs.Dayjs | null,
    public trangThai?: string | null,
    public comment?: string | null,
    public groupName?: string | null,
    public comment2?: string | null,
    public approverBy?: string | null,
    public branch?: string | null,
    public productType?: string | null,
    public deletedAt?: dayjs.Dayjs | null,
    public deletedBy?: string | null,
    public destinationWarehouse?: number | null,
    public chiTietLenhSanXuats?: IChiTietLenhSanXuat[] | null,
  ) {}
}

export function getLenhSanXuatIdentifier(
  lenhSanXuat: ILenhSanXuat,
): number | undefined {
  return lenhSanXuat.id;
}
