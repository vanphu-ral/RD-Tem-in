import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ApproveTemNccComponent } from "./approve-tem-ncc.component";

describe("ApproveTemNccComponentComponent", () => {
  let component: ApproveTemNccComponent;
  let fixture: ComponentFixture<ApproveTemNccComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApproveTemNccComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ApproveTemNccComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
