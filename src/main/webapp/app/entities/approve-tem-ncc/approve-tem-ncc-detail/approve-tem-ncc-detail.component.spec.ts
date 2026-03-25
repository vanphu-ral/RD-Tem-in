import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ApproveTemNccDetailComponent } from "./approve-tem-ncc-detail.component";

describe("ApproveTemNccDetailComponentComponent", () => {
  let component: ApproveTemNccDetailComponent;
  let fixture: ComponentFixture<ApproveTemNccDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApproveTemNccDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ApproveTemNccDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
