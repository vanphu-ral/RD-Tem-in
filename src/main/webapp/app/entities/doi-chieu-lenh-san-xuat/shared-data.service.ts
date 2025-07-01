import { Injectable } from '@angular/core';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  // yêu cầu một giá trị khởi tạo và tạo ra giá trị hiện tại của nó cho các subscriber mới
  // giữ và phát ra các giá trị thuộc kiểu number
  totalPassSource = new BehaviorSubject<number>(0);
  totalFailSource = new BehaviorSubject<number>(0);

  totalPass = this.totalPassSource.asObservable();
  totalFail = this.totalFailSource.asObservable();
  constructor(protected applicationConfigService: ApplicationConfigService) {}

  changeTotalPass(totalPass: number): void {
    this.totalPassSource.next(totalPass);
  }

  changeTotalFail(totalFail: number): void {
    this.totalFailSource.next(totalFail);
  }
}
