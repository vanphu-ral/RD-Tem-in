import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
})
export class FilterPipe implements PipeTransform {
  /**
   * @param items  Mảng cần lọc (object hoặc primitive)
   * @param searchText Chuỗi tìm kiếm
   * @param fields  (tùy chọn) Danh sách field object để so sánh
   */
  transform<T>(items: T[] | null, searchText: string, fields?: (keyof T)[]): T[] {
    if (!items) {
      return [];
    }
    if (!searchText) {
      return items;
    }

    const text = searchText.trim().toLowerCase();

    return items.filter(item => {
      // Nếu có fields, chỉ check trên các trường đó
      if (fields?.length) {
        return fields.some(field => {
          const val = (item[field] as any)?.toString().toLowerCase();
          return val?.includes(text);
        });
      }
      // Mặc định, stringify cả object để tìm
      const json = JSON.stringify(item).toLowerCase();
      return json.includes(text);
    });
  }
}
