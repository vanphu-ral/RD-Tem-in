import { Injectable, Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timestampToDate',
  standalone: true,
})
@Injectable({ providedIn: 'root' })
export class TimestampToDatePipe implements PipeTransform {
  transform(value: any, locale: string = 'vi-VN'): string {
    if (!value) {
      return '';
    }

    let date: Date;

    // Nếu value là chuỗi
    if (typeof value === 'string') {
      // Nếu chuỗi chứa dấu "[" (ví dụ: vết thêm [Asia/Bangkok])
      const bracketIndex = value.indexOf('[');
      if (bracketIndex > -1) {
        // Loại bỏ phần [Asia/Bangkok]
        value = value.substring(0, bracketIndex);
      }
      // Nếu value là chuỗi số và nhỏ hơn 1e12, xem đó là timestamp theo giây
      if (!isNaN(Number(value)) && Number(value) < 1e12) {
        date = new Date(Number(value) * 1000);
      } else {
        //chuyển chuỗi thành đối tượng Date
        date = new Date(value);
      }
    } else if (typeof value === 'number') {
      // Nếu là số và nhỏ hơn 1e12, xử lý như timestamp theo giây
      if (value < 1e12) {
        date = new Date(value * 1000);
      } else {
        date = new Date(value);
      }
    } else {
      date = new Date(value);
    }

    if (isNaN(date.getTime())) {
      return 'Invalid Date';
    }

    // Định dạng ngày: "dd/MM/yyyy"
    const optionsDate: Intl.DateTimeFormatOptions = {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    };

    // Định dạng thời gian: "HH:mm:ss"
    const optionsTime: Intl.DateTimeFormatOptions = {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    };

    const datePart = date.toLocaleDateString(locale, optionsDate);
    const timePart = date.toLocaleTimeString(locale, optionsTime);

    return `${datePart}`;
  }
}
