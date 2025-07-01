import { ILenhSanXuat } from 'app/entities/lenh-san-xuat/lenh-san-xuat.model';
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChiTietLenhSanXuat, getChiTietLenhSanXuatIdentifier } from '../chi-tiet-lenh-san-xuat.model';

export type EntityResponseType = HttpResponse<IChiTietLenhSanXuat>;
export type EntityResponseType1 = HttpResponse<ILenhSanXuat>;
export type EntityArrayResponseType = HttpResponse<IChiTietLenhSanXuat[]>;

@Injectable({ providedIn: 'root' })
export class ChiTietLenhSanXuatService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/chi-tiet-lenh-san-xuats');
  protected resourceUrl1 = this.applicationConfigService.getEndpointFor('api/lenh-san-xuats');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(chiTietLenhSanXuat: IChiTietLenhSanXuat): Observable<EntityResponseType> {
    const copy = chiTietLenhSanXuat;
    return this.http.post<IChiTietLenhSanXuat>(this.resourceUrl, copy, { observe: 'response' });
  }

  update(chiTietLenhSanXuat: IChiTietLenhSanXuat): Observable<EntityResponseType> {
    const copy = chiTietLenhSanXuat;
    return this.http.put<IChiTietLenhSanXuat>(
      `${this.resourceUrl}/${getChiTietLenhSanXuatIdentifier(chiTietLenhSanXuat) as number}`,
      copy,
      {
        observe: 'response',
      },
    );
  }

  partialUpdate(chiTietLenhSanXuat: IChiTietLenhSanXuat): Observable<EntityResponseType> {
    const copy = chiTietLenhSanXuat;
    return this.http.patch<IChiTietLenhSanXuat>(
      `${this.resourceUrl}/${getChiTietLenhSanXuatIdentifier(chiTietLenhSanXuat) as number}`,
      copy,
      {
        observe: 'response',
      },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IChiTietLenhSanXuat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
  // lay thong tin lenh san xuat
  find1(id: number): Observable<EntityResponseType1> {
    return this.http.get<ILenhSanXuat>(`${this.resourceUrl1}/${id}`, { observe: 'response' });
  }
  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IChiTietLenhSanXuat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addChiTietLenhSanXuatToCollectionIfMissing(
    chiTietLenhSanXuatCollection: IChiTietLenhSanXuat[],
    ...chiTietLenhSanXuatsToCheck: (IChiTietLenhSanXuat | null | undefined)[]
  ): IChiTietLenhSanXuat[] {
    const chiTietLenhSanXuats: IChiTietLenhSanXuat[] = chiTietLenhSanXuatsToCheck.filter(isPresent);
    if (chiTietLenhSanXuats.length > 0) {
      const chiTietLenhSanXuatCollectionIdentifiers = chiTietLenhSanXuatCollection.map(
        chiTietLenhSanXuatItem => getChiTietLenhSanXuatIdentifier(chiTietLenhSanXuatItem)!,
      );
      const chiTietLenhSanXuatsToAdd = chiTietLenhSanXuats.filter(chiTietLenhSanXuatItem => {
        const chiTietLenhSanXuatIdentifier = getChiTietLenhSanXuatIdentifier(chiTietLenhSanXuatItem);
        if (chiTietLenhSanXuatIdentifier == null || chiTietLenhSanXuatCollectionIdentifiers.includes(chiTietLenhSanXuatIdentifier)) {
          return false;
        }
        chiTietLenhSanXuatCollectionIdentifiers.push(chiTietLenhSanXuatIdentifier);
        return true;
      });
      return [...chiTietLenhSanXuatsToAdd, ...chiTietLenhSanXuatCollection];
    }
    return chiTietLenhSanXuatCollection;
  }
}
