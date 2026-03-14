import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AddInfoTemNccComponent } from "./add-info-tem-ncc.component";

describe("AddInfoTemNccComponentComponent", () => {
  let component: AddInfoTemNccComponent;
  let fixture: ComponentFixture<AddInfoTemNccComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddInfoTemNccComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AddInfoTemNccComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
