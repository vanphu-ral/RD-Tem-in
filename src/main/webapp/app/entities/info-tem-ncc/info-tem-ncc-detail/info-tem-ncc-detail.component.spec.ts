import { ComponentFixture, TestBed } from "@angular/core/testing";

import { InfoTemNccDetailComponent } from "./info-tem-ncc.-detail.component";

describe("InfoTemNccDetailComponentComponent", () => {
  let component: InfoTemNccDetailComponent;
  let fixture: ComponentFixture<InfoTemNccDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfoTemNccDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(InfoTemNccDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
