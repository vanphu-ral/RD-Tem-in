import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map } from "rxjs";
import {
  GenerateTemResponse,
  ListRequestCreateTem,
  MaterialItem,
  TemDetail,
} from "../detail/generate-tem-in-detail.component";
import { Apollo, gql } from "apollo-angular";

@Injectable({
  providedIn: "root",
})
export class GenerateTemInService {
  constructor(private apollo: Apollo) {}

  //lấy tất cả bản ghi trong bảng list_product_of_request
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

  //thông báo sau khi render tem
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
  /**
   * Query: Lấy TẤT CẢ tem details (toàn bộ bảng info_tem_detail)
   */
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
