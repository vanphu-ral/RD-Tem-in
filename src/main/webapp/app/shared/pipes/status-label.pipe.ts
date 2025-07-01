import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusLabel',
  standalone: true, // Nếu dùng standalone component, có thể khai báo standalone
})
export class StatusLabelPipe implements PipeTransform {
  transform(value: string): string {
    if (value == null) {
      return 'N/A';
    }
    const statusStr = value != null ? value.toString().trim() : '';
    // console.log('Status value in pipe:', statusStr);
    switch (statusStr) {
      case '3':
        return 'Available';
      case '6':
        return 'Consumed';
      case '19':
        return 'Expired';
      default:
        return statusStr;
    }
  }
}
