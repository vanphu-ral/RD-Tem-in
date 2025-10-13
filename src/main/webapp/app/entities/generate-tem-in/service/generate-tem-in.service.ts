import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Apollo, gql, QueryRef } from "apollo-angular";
import { Observable, map } from "rxjs";
import {
  ListRequestCreateTem,
  ListRequestCreateTemRequest,
} from "../models/list-request-create-tem.model";
import {
  ListProductOfRequest,
  ListProductOfRequestRequest,
  ExcelImportData,
} from "../models/list-product-of-request.model";
import {
  GenerateTemResponse,
  MaterialItem,
  TemDetail,
} from "../detail/generate-tem-in-detail.component";

const GET_REQUESTS_QUERY = gql`
  query GetRequests {
    listRequestCreateTems {
      id
      vendor
      userData5
      createdBy
      numberProduction
      createdDate
      totalQuantity
      status
    }
  }
`;

const GET_PRODUCTS_BY_REQUEST_QUERY = gql`
  query GetProductsByRequest($requestId: ID!) {
    requestList(page: 0, size: 1) {
      id
      products(page: 0, size: 100) {
        id
        Request_create_tem_id
        SAPCode
        Tem_quantity
        PartNumber
        LOT
        InitialQuantity
        Vendor
        UserData1
        UserData2
        UserData3
        UserData4
        UserData5
        StorageUnit
        ExpirationDate
        ManufacturingDate
        Arrival_date
        Number_of_prints
      }
    }
  }
`;

const CREATE_PRODUCT_MUTATION = gql`
  mutation CreateProduct($input: CreateProductInput!) {
    createProduct(input: $input) {
      id
      Request_create_tem_id
      SAPCode
      Tem_quantity
      PartNumber
      LOT
      InitialQuantity
      Vendor
      UserData1
      UserData2
      UserData3
      UserData4
      UserData5
      StorageUnit
      ExpirationDate
      ManufacturingDate
      Arrival_date
      Number_of_prints
    }
  }
`;

type GetRequestsResult = {
  listRequestCreateTems: ListRequestCreateTem[];
};

type GetProductsByRequestResult = {
  requestList: Array<{
    id: string | number;
    products: ListProductOfRequest[];
  }>;
};

type CreateProductResult = {
  createProduct: ListProductOfRequest;
};

@Injectable({ providedIn: "root" })
export class GenerateTemInService {
  private readonly baseUrl = "/api/generate-tem-in";

  constructor(
    private http: HttpClient,
    private apollo: Apollo,
  ) {
    console.log("GenerateTemInService initialized");
  }

  getRequestList(): Observable<ListRequestCreateTem[]> {
    const GET_REQUESTS = gql`
      query {
        listRequestCreateTems {
          id
          vendor
          userData5
          status
        }
      }
    `;

    return this.apollo
      .watchQuery<{ listRequestCreateTems: ListRequestCreateTem[] }>({
        query: GET_REQUESTS,
      })
      .valueChanges.pipe(map((result) => result.data.listRequestCreateTems));
  }

  getAllRequests(params?: {
    status?: string;
    vendor?: string;
    createdBy?: string;
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<ListRequestCreateTem[]> {
    return this.apollo
      .query<GetRequestsResult>({
        query: GET_REQUESTS_QUERY,
        fetchPolicy: "network-only",
      })
      .pipe(
        map(
          (result) =>
            result.data.listRequestCreateTems as ListRequestCreateTem[],
        ),
      );
  }

  getRequestById(id: number): Observable<ListRequestCreateTem> {
    return this.http.get<ListRequestCreateTem>(
      `${this.baseUrl}/requests/${id}`,
    );
  }

  createRequest(
    request: ListRequestCreateTemRequest,
  ): Observable<ListRequestCreateTem> {
    return this.http.post<ListRequestCreateTem>(
      `${this.baseUrl}/requests`,
      request,
    );
  }

  updateRequest(
    id: number,
    request: ListRequestCreateTemRequest,
  ): Observable<ListRequestCreateTem> {
    return this.http.put<ListRequestCreateTem>(
      `${this.baseUrl}/requests/${id}`,
      request,
    );
  }

  deleteRequest(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/requests/${id}`);
  }

  getMaterialItems(): Observable<MaterialItem[]> {
    const GET_PRODUCTS = gql`
      query {
        listProductOfRequests {
          id
          requestCreateTemId
          sapCode
          partNumber
          lot
          initialQuantity
          vendor
          userData1
          userData2
          userData3
          userData4
          userData5
          storageUnit
          expirationDate
          manufacturingDate
          arrivalDate
          numberOfPrints
        }
      }
    `;

    return this.apollo
      .watchQuery<{ listProductOfRequests: any[] }>({
        query: GET_PRODUCTS,
      })
      .valueChanges.pipe(
        map((result) =>
          result.data.listProductOfRequests.map((item, index) => ({
            stt: index + 1,
            id: item.id,
            productOfRequestId: item.requestCreateTemId,
            reelId: "",
            sapCode: item.sapCode,
            productName: "",
            partNumber: item.partNumber,
            lot: item.lot,
            initialQuantity: item.initialQuantity,
            vendor: item.vendor,
            userData1: item.userData1,
            userData2: item.userData2,
            userData3: item.userData3,
            userData4: item.userData4,
            userData5: item.userData5,
            storageUnit: item.storageUnit,
            expirationDate: item.expirationDate,
            manufacturingDate: item.manufacturingDate,
            arrivalDate: item.arrivalDate,
            qrCode: "",
            lanIn: 1,
            soTem: 1,
          })),
        ),
      );
  }

  getProductsByRequestId(
    requestId: number,
  ): Observable<ListProductOfRequest[]> {
    return this.apollo
      .query<GetProductsByRequestResult>({
        query: GET_PRODUCTS_BY_REQUEST_QUERY,
        variables: { requestId: String(requestId) },
        fetchPolicy: "network-only",
      })
      .pipe(
        map((result) => {
          const requests = result.data.requestList;
          if (Array.isArray(requests) && requests.length > 0) {
            return (requests[0].products ?? []) as ListProductOfRequest[];
          }
          return [] as ListProductOfRequest[];
        }),
      );
  }

  createProducts(
    requestId: number,
    products: ListProductOfRequestRequest[],
  ): Observable<ListProductOfRequest[]> {
    return this.http.post<ListProductOfRequest[]>(
      `${this.baseUrl}/requests/${requestId}/products`,
      products,
    );
  }

  importProductsFromExcel(
    requestId: number,
    excelData: ExcelImportData[],
  ): Observable<ListProductOfRequest[]> {
    return this.http.post<ListProductOfRequest[]>(
      `${this.baseUrl}/requests/${requestId}/import-excel`,
      excelData,
    );
  }

  updateProduct(
    id: number,
    product: ListProductOfRequestRequest,
  ): Observable<ListProductOfRequest> {
    return this.http.put<ListProductOfRequest>(
      `${this.baseUrl}/products/${id}`,
      product,
    );
  }

  /**
   * Delete a specific product (REST)
   */
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/products/${id}`);
  }

  getRequestStats(): Observable<{
    totalRequests: number;
    totalProducts: number;
    statusCounts: { [status: string]: number };
  }> {
    return this.http.get<{
      totalRequests: number;
      totalProducts: number;
      statusCounts: { [status: string]: number };
    }>(`${this.baseUrl}/stats`);
  }

  exportRequestToExcel(requestId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/requests/${requestId}/export`, {
      responseType: "blob",
    });
  }

  generateTem(storageUnit: string): Observable<GenerateTemResponse> {
    const GENERATE_TEM = gql`
      mutation ($storageUnit: String!) {
        generateTem(storageUnit: $storageUnit) {
          success
          message
          totalTems
        }
      }
    `;

    return this.apollo
      .mutate<{ generateTem: GenerateTemResponse }>({
        mutation: GENERATE_TEM,
        variables: { storageUnit },
      })
      .pipe(
        map(
          (result) =>
            result.data?.generateTem ?? {
              success: false,
              message: "Không nhận được response",
              totalTems: 0,
            },
        ),
      );
  }
  getTemDetailsByProductId(productId: number): Observable<TemDetail[]> {
    const GET_TEM_DETAILS = gql`
      query GetTemDetailsByProductId($productId: Int!) {
        infoTemDetailsByProductId(productId: $productId) {
          id
          productOfRequestId
          reelId
          sapCode
          productName
          partNumber
          lot
          initialQuantity
          vendor
          userData1
          userData2
          userData3
          userData4
          userData5
          storageUnit
          expirationDate
          manufacturingDate
          arrivalDate
          qrCode
          slTemQuantity
        }
      }
    `;

    return this.apollo
      .watchQuery<{ infoTemDetailsByProductId: TemDetail[] }>({
        query: GET_TEM_DETAILS,
        variables: { productId },
        fetchPolicy: "network-only",
      })
      .valueChanges.pipe(
        map((result) => result.data.infoTemDetailsByProductId),
      );
  }
  getAllTemDetails(): Observable<TemDetail[]> {
    const GET_ALL_TEM_DETAILS = gql`
      query GetAllTemDetails {
        infoTemDetails {
          id
          productOfRequestId
          reelId
          sapCode
          productName
          partNumber
          lot
          initialQuantity
          vendor
          userData1
          userData2
          userData3
          userData4
          userData5
          storageUnit
          expirationDate
          manufacturingDate
          arrivalDate
          qrCode
          slTemQuantity
        }
      }
    `;

    return this.apollo
      .watchQuery<{ infoTemDetails: TemDetail[] }>({
        query: GET_ALL_TEM_DETAILS,
        fetchPolicy: "network-only",
      })
      .valueChanges.pipe(map((result) => result.data.infoTemDetails));
  }
}
