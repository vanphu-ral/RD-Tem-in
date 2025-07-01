import { IChiTietLenhSanXuat } from 'app/entities/chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.model';
import dayjs from 'dayjs/esm';

export interface ILenhSanXuat {
  id?: number;
  maLenhSanXuat?: string;
  sapCode?: string | null;
  sapName?: string | null;
  workOrderCode?: string | null;
  version?: string | null;
  storageCode?: string | null;
  totalQuantity?: string | null;
  createBy?: string | null;
  entryTime?: dayjs.Dayjs | null;
  timeUpdate?: dayjs.Dayjs | null;
  trangThai?: string | null;
  comment?: string | null;
  groupName?: string | null;
  comment2?: string | null;
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
    public totalQuantity?: string | null,
    public createBy?: string | null,
    public entryTime?: dayjs.Dayjs | null,
    public timeUpdate?: dayjs.Dayjs | null,
    public trangThai?: string | null,
    public comment?: string | null,
    public groupName?: string | null,
    public comment2?: string | null,
    public chiTietLenhSanXuats?: IChiTietLenhSanXuat[] | null,
  ) {}
}

export function getLenhSanXuatIdentifier(lenhSanXuat: ILenhSanXuat): number | undefined {
  return lenhSanXuat.id;
}
