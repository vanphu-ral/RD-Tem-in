import dayjs from "dayjs/esm";
import { ILenhSanXuat } from "app/entities/lenh-san-xuat/lenh-san-xuat.model";

export interface IChiTietLenhSanXuat {
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
  lenhSanXuat?: ILenhSanXuat | null;
}

export class ChiTietLenhSanXuat implements IChiTietLenhSanXuat {
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
    public lenhSanXuat?: ILenhSanXuat | null,
  ) {}
}

export function getChiTietLenhSanXuatIdentifier(
  chiTietLenhSanXuat: IChiTietLenhSanXuat,
): number | undefined {
  return chiTietLenhSanXuat.id;
}
