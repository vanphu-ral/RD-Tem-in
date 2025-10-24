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
export interface UpdateResponse {
  success: boolean;
  message: string;
}

// @Injectable({
//   providedIn: "root",
// })

const DELETE_REQUEST_MUTATION = gql`
  mutation DeleteRequest($requestId: Int!) {
    deleteRequest(requestId: $requestId) {
      success
      message
    }
  }
`;

const GET_REQUESTS_QUERY = gql`
  query GetRequests {
    listRequestCreateTems {
      id
      vendor
      vendorName
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
  query GetProductsByRequest($requestId: Int!) {
    getProductOfRequestByRequestId(requestId: $requestId) {
      id
      requestCreateTemId
      sapCode
      temQuantity
      partNumber
      productName
      lot
      initialQuantity
      vendor
      vendorName
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
      UploadPanacim
    }
  }
`;

const CREATE_PRODUCT_MUTATION = gql`
  mutation CreateProduct($input: CreateProductInput!) {
    createProduct(input: $input) {
      id
      requestCreateTemId
      sapCode
      productName
      temQuantity
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

const CREATE_PRODUCTS_BATCH_MUTATION = gql`
  mutation CreateProductsBatch(
    $requestId: Int!
    $products: [CreateProductInput!]!
  ) {
    createProductsBatch(requestId: $requestId, products: $products) {
      id
      requestCreateTemId
      sapCode
      productName
      temQuantity
      partNumber
      lot
      initialQuantity
      vendor
      vendorName
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

const CREATE_REQUEST_AND_PRODUCTS_MUTATION = gql`
  mutation CreateRequestAndProducts($input: CreateRequestWithProductsInput!) {
    createRequestAndProducts(input: $input) {
      requestId
      products {
        id
        requestCreateTemId
        sapCode
        productName
        temQuantity
        partNumber
        lot
        initialQuantity
        vendor
        vendorName
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
      message
    }
  }
`;

const UPDATE_REQUEST_PRODUCTS_MUTATION = gql`
  mutation UpdateRequestProducts(
    $requestId: Int!
    $products: [CreateProductInput!]!
  ) {
    updateRequestProducts(requestId: $requestId, products: $products) {
      id
      requestCreateTemId
      sapCode
      productName
      temQuantity
      partNumber
      lot
      initialQuantity
      vendor
      vendorName
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

type GetRequestsResult = {
  listRequestCreateTems: ListRequestCreateTem[];
};

type GetProductsByRequestResult = {
  getProductOfRequestByRequestId: MaterialItem[];
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
    createdDate?: string;
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

  deleteRequest(id: number): Observable<UpdateResponse> {
    return this.apollo
      .mutate<{ deleteRequest: UpdateResponse }>({
        mutation: DELETE_REQUEST_MUTATION,
        variables: { requestId: id },
      })
      .pipe(map((res) => res.data!.deleteRequest));
  }

  getMaterialItems(): Observable<MaterialItem[]> {
    const GET_PRODUCTS = gql`
      query {
        listProductOfRequests {
          id
          requestCreateTemId
          sapCode
          productName
          temQuantity
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
            productName: item.productName,
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
            requestCreateTemId: item.requestCreateTemId,
            temQuantity: item.temQuantity,
            numberOfPrints: item.numberOfPrints,
          })),
        ),
      );
  }

  getProductsByRequestId(requestId: number): Observable<MaterialItem[]> {
    return this.apollo
      .query<GetProductsByRequestResult>({
        query: GET_PRODUCTS_BY_REQUEST_QUERY,
        variables: { requestId },
        fetchPolicy: "network-only",
      })
      .pipe(
        map(
          (result): MaterialItem[] =>
            result.data.getProductOfRequestByRequestId as MaterialItem[],
        ),
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

  createRequestAndProducts(
    vendor: string,
    vendorName: string,
    userData5: string,
    createdBy: string,
    products: ExcelImportData[],
  ): Observable<{
    requestId: number;
    products: ListProductOfRequest[];
    message: string;
  }> {
    // Convert ExcelImportData to CreateProductInput format with validation
    const productInputs = products.map((item, index) => {
      // Validate required fields
      if (!item.sapCode || item.sapCode.trim() === "") {
        throw new Error(`Row ${index + 1}: SAP Code is required`);
      }
      if (!item.partNumber || item.partNumber.trim() === "") {
        throw new Error(`Row ${index + 1}: Part Number is required`);
      }
      if (!item.lot || item.lot.trim() === "") {
        throw new Error(`Row ${index + 1}: LOT is required`);
      }
      if (!item.vendor || item.vendor.trim() === "") {
        throw new Error(`Row ${index + 1}: Vendor is required`);
      }
      if (!item.tenNCC || item.tenNCC.trim() === "") {
        throw new Error(`Row ${index + 1}: Vendor name is required`);
      }
      if (!item.temQuantity || item.temQuantity <= 0) {
        throw new Error(
          `Row ${index + 1}: Tem Quantity must be greater than 0`,
        );
      }
      if (!item.initialQuantity || item.initialQuantity <= 0) {
        throw new Error(
          `Row ${index + 1}: Initial Quantity must be greater than 0`,
        );
      }
      if (!item.expirationDate || item.expirationDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Expiration Date is required`);
      }
      if (!item.manufacturingDate || item.manufacturingDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Manufacturing Date is required`);
      }
      if (!item.arrivalDate || item.arrivalDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Arrival Date is required`);
      }

      return {
        sapCode: item.sapCode.trim(),
        productName: item.tenSP?.trim() ?? "",
        temQuantity: item.temQuantity,
        partNumber: item.partNumber.trim(),
        lot: item.lot.trim(),
        initialQuantity: item.initialQuantity,
        vendor: item.vendor.trim(),
        vendorName: item.tenNCC.trim(),
        userData1: item.userData1 ?? "",
        userData2: item.userData2 ?? "",
        userData3: item.userData3 ?? "",
        userData4: item.userData4 ?? "",
        userData5: item.userData5 ?? "",
        storageUnit: item.storageUnit ?? "",
        expirationDate: item.expirationDate.trim(),
        manufacturingDate: item.manufacturingDate.trim(),
        arrivalDate: item.arrivalDate.trim(),
      };
    });

    const input = {
      vendor,
      vendorName,
      userData5,
      createdBy,
      createdDate: new Date().toISOString().slice(0, 10),
      products: productInputs,
    };

    return this.apollo
      .mutate<{
        createRequestAndProducts: {
          requestId: number;
          products: ListProductOfRequest[];
          message: string;
        };
      }>({
        mutation: CREATE_REQUEST_AND_PRODUCTS_MUTATION,
        variables: { input },
      })
      .pipe(
        map((result) => {
          if (!result.data || !result.data.createRequestAndProducts) {
            throw new Error("No data returned from server");
          }
          return result.data.createRequestAndProducts;
        }),
      );
  }

  updateRequestProducts(
    requestId: number,
    products: ExcelImportData[],
  ): Observable<ListProductOfRequest[]> {
    // Convert ExcelImportData to CreateProductInput format with validation
    const productInputs = products.map((item, index) => {
      // Validate required fields
      if (!item.sapCode || item.sapCode.trim() === "") {
        throw new Error(`Row ${index + 1}: SAP Code is required`);
      }
      if (!item.partNumber || item.partNumber.trim() === "") {
        throw new Error(`Row ${index + 1}: Part Number is required`);
      }
      if (!item.lot || item.lot.trim() === "") {
        throw new Error(`Row ${index + 1}: LOT is required`);
      }
      if (!item.vendor || item.vendor.trim() === "") {
        throw new Error(`Row ${index + 1}: Vendor is required`);
      }
      if (!item.tenNCC || item.tenNCC.trim() === "") {
        throw new Error(`Row ${index + 1}: Vendor name is required`);
      }
      if (!item.temQuantity || item.temQuantity <= 0) {
        throw new Error(
          `Row ${index + 1}: Tem Quantity must be greater than 0`,
        );
      }
      if (!item.initialQuantity || item.initialQuantity <= 0) {
        throw new Error(
          `Row ${index + 1}: Initial Quantity must be greater than 0`,
        );
      }
      if (!item.expirationDate || item.expirationDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Expiration Date is required`);
      }
      if (!item.manufacturingDate || item.manufacturingDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Manufacturing Date is required`);
      }
      if (!item.arrivalDate || item.arrivalDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Arrival Date is required`);
      }

      return {
        sapCode: item.sapCode.trim(),
        productName: item.tenSP?.trim() ?? "",
        temQuantity: item.temQuantity,
        partNumber: item.partNumber.trim(),
        lot: item.lot.trim(),
        initialQuantity: item.initialQuantity,
        vendor: item.vendor.trim(),
        vendorName: item.tenNCC.trim(),
        userData1: item.userData1 ?? "",
        userData2: item.userData2 ?? "",
        userData3: item.userData3 ?? "",
        userData4: item.userData4 ?? "",
        userData5: item.userData5 ?? "",
        storageUnit: item.storageUnit ?? "",
        expirationDate: item.expirationDate.trim(),
        manufacturingDate: item.manufacturingDate.trim(),
        arrivalDate: item.arrivalDate.trim(),
      };
    });

    return this.apollo
      .mutate<{ updateRequestProducts: ListProductOfRequest[] }>({
        mutation: UPDATE_REQUEST_PRODUCTS_MUTATION,
        variables: {
          requestId: requestId,
          products: productInputs,
        },
      })
      .pipe(
        map((result) => {
          if (!result.data || !result.data.updateRequestProducts) {
            throw new Error("No data returned from server");
          }
          return result.data.updateRequestProducts;
        }),
      );
  }

  importProductsFromExcel(
    requestId: number,
    excelData: ExcelImportData[],
  ): Observable<ListProductOfRequest[]> {
    // Validate input data
    if (!excelData || excelData.length === 0) {
      throw new Error("No data to import");
    }

    // Convert ExcelImportData to CreateProductInput format with validation
    const products = excelData.map((item, index) => {
      // Validate required fields
      if (!item.sapCode || item.sapCode.trim() === "") {
        throw new Error(`Row ${index + 1}: SAP Code is required`);
      }
      if (!item.tenSP || item.tenSP.trim() === "") {
        throw new Error(`Row ${index + 1}: Product Name is required`);
      }
      if (!item.partNumber || item.partNumber.trim() === "") {
        throw new Error(`Row ${index + 1}: Part Number is required`);
      }
      if (!item.lot || item.lot.trim() === "") {
        throw new Error(`Row ${index + 1}: LOT is required`);
      }
      if (!item.vendor || item.vendor.trim() === "") {
        throw new Error(`Row ${index + 1}: Vendor is required`);
      }
      if (!item.temQuantity || item.temQuantity <= 0) {
        throw new Error(
          `Row ${index + 1}: Tem Quantity must be greater than 0`,
        );
      }
      if (!item.initialQuantity || item.initialQuantity <= 0) {
        throw new Error(
          `Row ${index + 1}: Initial Quantity must be greater than 0`,
        );
      }
      if (!item.expirationDate || item.expirationDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Expiration Date is required`);
      }
      if (!item.manufacturingDate || item.manufacturingDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Manufacturing Date is required`);
      }
      if (!item.arrivalDate || item.arrivalDate.trim() === "") {
        throw new Error(`Row ${index + 1}: Arrival Date is required`);
      }

      return {
        requestCreateTemId: requestId,
        sapCode: item.sapCode.trim(),
        productName: item.tenSP?.trim() ?? "",
        temQuantity: item.temQuantity,
        partNumber: item.partNumber.trim(),
        lot: item.lot.trim(),
        initialQuantity: item.initialQuantity,
        vendor: item.vendor.trim(),
        userData1: item.userData1 ?? "",
        userData2: item.userData2 ?? "",
        userData3: item.userData3 ?? "",
        userData4: item.userData4 ?? "",
        userData5: item.userData5 ?? "",
        storageUnit: item.storageUnit ?? "",
        expirationDate: item.expirationDate.trim(),
        manufacturingDate: item.manufacturingDate.trim(),
        arrivalDate: item.arrivalDate.trim(),
      };
    });

    return this.apollo
      .mutate<{ createProductsBatch: ListProductOfRequest[] }>({
        mutation: CREATE_PRODUCTS_BATCH_MUTATION,
        variables: {
          requestId: requestId,
          products: products,
        },
      })
      .pipe(
        map((result) => {
          if (!result.data || !result.data.createProductsBatch) {
            throw new Error("No data returned from server");
          }
          return result.data.createProductsBatch;
        }),
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
  //lưu kho
  updateStorageUnitForRequest(
    requestId: number,
    storageUnit: string,
  ): Observable<UpdateResponse> {
    const UPDATE_STORAGE_UNIT = gql`
      mutation ($requestId: Int!, $storageUnit: String!) {
        updateStorageUnitForRequest(
          requestId: $requestId
          storageUnit: $storageUnit
        ) {
          success
          message
        }
      }
    `;

    return this.apollo
      .mutate<{ updateStorageUnitForRequest: UpdateResponse }>({
        mutation: UPDATE_STORAGE_UNIT,
        variables: { requestId, storageUnit },
      })
      .pipe(
        map(
          (result) =>
            result.data?.updateStorageUnitForRequest ?? {
              success: false,
              message: "Không nhận được phản hồi từ server",
            },
        ),
      );
  }

  //thông báo sau khi render tem
  generateTemByRequest(requestId: number): Observable<GenerateTemResponse> {
    const GENERATE_TEM_BY_REQUEST = gql`
      mutation ($requestId: Int!) {
        generateTem(requestId: $requestId) {
          success
          message
          totalTems
        }
      }
    `;

    return this.apollo
      .mutate<{ generateTem: GenerateTemResponse }>({
        mutation: GENERATE_TEM_BY_REQUEST,
        variables: { requestId },
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

  //Lấy chi tiết tem detai theo Request_create_tem_id của list_product_of_request
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
  getTemDetailsByRequestId(requestId: number): Observable<TemDetail[]> {
    const GET_ALL_TEM_DETAILS = gql`
      query GetTemDetailsByRequestId($requestId: Int!) {
        infoTemDetailsByRequestId(requestId: $requestId) {
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
        }
      }
    `;

    return this.apollo
      .watchQuery<{ infoTemDetailsByRequestId: TemDetail[] }>({
        query: GET_ALL_TEM_DETAILS,
        variables: { requestId },
        fetchPolicy: "network-only",
      })
      .valueChanges.pipe(
        map((result) => result.data.infoTemDetailsByRequestId),
      );
  }
  /**
   * Query: Chỉnh sửa bảng list_product_of_request
   */
  updateProductOfRequest(product: any): Observable<any> {
    const UPDATE_PRODUCT_MUTATION = gql`
      mutation UpdateProductOfRequest($input: UpdateProductInput!) {
        updateProductOfRequest(input: $input) {
          success
          message
        }
      }
    `;

    return this.apollo.mutate({
      mutation: UPDATE_PRODUCT_MUTATION,
      variables: {
        input: {
          id: Number(product.id),
          sapCode: product.sapCode,
          requestCreateTemId: product.requestCreateTemId,
          partNumber: product.partNumber,
          lot: product.lot,
          temQuantity: product.temQuantity,
          initialQuantity: product.initialQuantity,
          vendor: product.vendor,
          vendorName: product.vendorName,
          userData1: product.userData1,
          userData2: product.userData2,
          userData3: product.userData3,
          userData4: product.userData4,
          userData5: product.userData5,
          storageUnit: product.storageUnit,
          expirationDate: product.expirationDate,
          manufacturingDate: product.manufacturingDate,
          arrivalDate: product.arrivalDate,
          numberOfPrints: product.numberOfPrints,
        },
      },
    });
  }
  //xoa req
  deleteReqById(productId: number): Observable<UpdateResponse> {
    console.log("Deleting productId:", productId);
    const DELETE_PRODUCT = gql`
      mutation DeleteProduct($productId: Int!) {
        deleteProduct(productId: $productId) {
          success
          message
        }
      }
    `;

    return this.apollo
      .mutate<{ deleteProduct: UpdateResponse }>({
        mutation: DELETE_PRODUCT,
        variables: { productId },
      })
      .pipe(map((res) => res.data!.deleteProduct));
  }

  //upload panacim
  updateUploadPanacim(productId: any): Observable<any> {
    const UPDATE_UPLOAD_STATUS = gql`
      mutation UpdateProductOfRequest($input: UpdateProductInput!) {
        updateProductOfRequest(input: $input) {
          success
          message
        }
      }
    `;

    return this.apollo.mutate({
      mutation: UPDATE_UPLOAD_STATUS,
      variables: {
        input: {
          id: Number(productId.id),
          sapCode: productId.sapCode,
          requestCreateTemId: productId.requestCreateTemId,
          partNumber: productId.partNumber,
          lot: productId.lot,
          temQuantity: productId.temQuantity,
          initialQuantity: productId.initialQuantity,
          vendor: productId.vendor,
          vendorName: productId.vendorName,
          userData1: productId.userData1,
          userData2: productId.userData2,
          userData3: productId.userData3,
          userData4: productId.userData4,
          userData5: productId.userData5,
          storageUnit: productId.storageUnit,
          expirationDate: productId.expirationDate,
          manufacturingDate: productId.manufacturingDate,
          arrivalDate: productId.arrivalDate,
          numberOfPrints: productId.numberOfPrints,
          UploadPanacim: true,
        },
      },
    });
  }
}
