/** Domain model — dùng trong component/service sau khi map từ GraphQL. */
export interface ListRequestCreateTem {
  id?: number;
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  numberProduction?: number;
  totalQuantity?: number;
  status?: string;
  createdBy?: string;
  createdDate?: string;
  type?: boolean;
  entryDate?: string;
  lastModifiedDate?: string;
  whsCode?: string;
}

export interface GetRequestsQueryParams {
  search?: string;
  status?: string;
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  createdBy?: string;
  createdDate?: string;
  page?: number;
  size?: number;
}

export interface ListRequestCreateTemPage {
  content: ListRequestCreateTem[];
  totalElements: number;
  page: number;
  size: number;
  totalPages: number;
}

export interface ListRequestCreateTemRequest {
  vendor?: string;
  vendorName?: string;
  userData5?: string;
  createdBy?: string;
  numberProduction?: number;
  totalQuantity?: number;
  status?: string;
  createdDate?: string;
  type?: boolean;
  entryDate?: string;
  /** Mã kho SAP cấp đơn — GraphQL field WhsCode. */
  WhsCode?: string;
}

/** Raw shape từ GraphQL type ListRequestCreateTem (schema.graphqls). */
export interface ListRequestCreateTemGraphql {
  id?: number | string | null;
  vendor?: string | null;
  vendorName?: string | null;
  userData5?: string | null;
  createdBy?: string | null;
  numberProduction?: number | null;
  totalQuantity?: number | null;
  status?: string | null;
  createdDate?: string | null;
  type?: boolean | null;
  entryDate?: string | null;
  WhsCode?: string | null;
}

/** Raw shape từ GraphQL type RequestCreateTemPage. */
export interface RequestCreateTemPageGraphql {
  content?: ListRequestCreateTemGraphql[] | null;
  totalElements?: number | null;
  page?: number | null;
  size?: number | null;
  totalPages?: number | null;
}

/** Chuẩn hóa filter GraphQL: trim, chuỗi rỗng → null (BE normalizeSearch). */
export function toGraphqlOptionalFilter(
  value: string | undefined,
): string | null {
  const trimmed = value?.trim();
  return trimmed ? trimmed : null;
}

export function mapListRequestCreateTemFromGraphql(
  raw: ListRequestCreateTemGraphql,
): ListRequestCreateTem {
  const whsCode = (raw.WhsCode ?? "").trim();
  const id =
    raw.id === null || raw.id === undefined || raw.id === ""
      ? undefined
      : Number(raw.id);

  return {
    id: Number.isNaN(id) ? undefined : id,
    vendor: raw.vendor ?? undefined,
    vendorName: raw.vendorName ?? undefined,
    userData5: raw.userData5 ?? undefined,
    createdBy: raw.createdBy ?? undefined,
    numberProduction: raw.numberProduction ?? undefined,
    totalQuantity: raw.totalQuantity ?? undefined,
    status: raw.status ?? undefined,
    createdDate: raw.createdDate ?? undefined,
    type: raw.type ?? undefined,
    entryDate: raw.entryDate ?? undefined,
    whsCode,
  };
}

export function mapRequestCreateTemPageFromGraphql(
  raw: RequestCreateTemPageGraphql | null | undefined,
  fallbackPage: number,
  fallbackSize: number,
): ListRequestCreateTemPage {
  const empty: ListRequestCreateTemPage = {
    content: [],
    totalElements: 0,
    page: fallbackPage,
    size: fallbackSize,
    totalPages: 0,
  };

  if (!raw || !Array.isArray(raw.content)) {
    return empty;
  }

  const content: ListRequestCreateTem[] = raw.content.map((item) =>
    mapListRequestCreateTemFromGraphql(item),
  );

  return {
    content,
    totalElements: Number(raw.totalElements) || 0,
    page: Number(raw.page ?? fallbackPage),
    size: Number(raw.size ?? fallbackSize),
    totalPages: Number(raw.totalPages) || 0,
  };
}
