import { ComponentFixture, TestBed } from "@angular/core/testing";

import { GenerateTemInComponent } from "./generate-tem-in.component";

describe("GenerateTemInComponent", () => {
  let component: GenerateTemInComponent;
  let fixture: ComponentFixture<GenerateTemInComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateTemInComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GenerateTemInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
