import { ComponentFixture, TestBed } from "@angular/core/testing";

import { InfoTemNccComponent } from "./info-tem-ncc.component";

describe("InfoTemNccComponentComponent", () => {
  let component: InfoTemNccComponent;
  let fixture: ComponentFixture<InfoTemNccComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfoTemNccComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(InfoTemNccComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
