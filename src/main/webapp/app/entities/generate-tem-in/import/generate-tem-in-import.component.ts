import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import {
  faFileImport,
  faEye,
  faEdit,
  faTrash,
  faPrint,
  faArrowUp,
  faSave,
  faPlusCircle,
} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: "jhi-generate-tem-in-import",
  templateUrl: "./generate-tem-in-import.component.html",
  styleUrl: "./generate-tem-in-import.component.scss",
  standalone: false,
})
export class GenerateTemInImportComponent implements OnInit {
  faFileImport = faFileImport;
  faEye = faEye;
  faEdit = faEdit;
  faTrash = faTrash;
  faPrint = faPrint;
  faArrowUp = faArrowUp;
  faPlusCircle = faPlusCircle;
  faSave = faSave;
  generateTemIn = null;
  selectedFile: File | null = null;
  selectedStorageUnit: string = "";
  storageUnits: string[] = ["SU01", "SU02", "SU03"];

  importData = [
    {
      sapCode: "SAP001",
      productName: "Diode-BZX55-C5V1",
      partNumber: "BZX55-C5V1",
      lot: "LOT001",
      storageUnit: "SU01",
      temNumber: "TEM001",
      initialQuantity: 1000,
      vendor: "VND001",
      userCreate: "Nguyễn Văn A",
    },
  ];
  @ViewChild("fileInput") fileInput!: ElementRef<HTMLInputElement>;
  constructor(protected activatedRoute: ActivatedRoute) {}
  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ generateTemIn }) => {
      this.generateTemIn = generateTemIn;
    });
  }

  selectFile(): void {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onFileDrop(event: DragEvent): void {
    event.preventDefault();
    if (event.dataTransfer?.files.length) {
      this.selectedFile = event.dataTransfer.files[0];
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
  }

  onImport(): void {
    if (!this.selectedFile) {
      return;
    }

    const formData = new FormData();
    formData.append("file", this.selectedFile);

    // Gửi formData đến API backend
    // Ví dụ:
    // this.http.post('/api/import-tem', formData).subscribe(...);

    console.log("Importing file:", this.selectedFile.name);
  }
  onSaveStorageUnit(): void {
    if (!this.selectedStorageUnit) {
      return;
    }
    console.log("Lưu kho chứa:", this.selectedStorageUnit);
    // Gọi API hoặc xử lý logic lưu kho
  }

  onCreateDetail(): void {
    console.log("Tạo thông tin chi tiết cho PO 12345");
    // Gọi API hoặc mở modal tạo chi tiết
  }
}
