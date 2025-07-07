import { NgMultiSelectDropDownModule } from "ng-multiselect-dropdown";
import { NgxPaginationModule } from "ngx-pagination";
import { ReactiveFormsModule } from "@angular/forms";
import { NgModule, LOCALE_ID, APP_INITIALIZER } from "@angular/core";
import { registerLocaleData } from "@angular/common";
import { HttpClientModule } from "@angular/common/http";
import locale from "@angular/common/locales/en";
import { BrowserModule, Title } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ServiceWorkerModule } from "@angular/service-worker";
import { FaIconLibrary } from "@fortawesome/angular-fontawesome";
import { EntityRoutingModule } from "./entities/entity-routing.module";
import { NgxWebstorageModule } from "ngx-webstorage";
import dayjs from "dayjs/esm";
import { MatMenuModule } from "@angular/material/menu";
import {
  NgbDateAdapter,
  NgbDatepickerConfig,
} from "@ng-bootstrap/ng-bootstrap";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import "./config/dayjs";
import { SharedModule } from "app/shared/shared.module";
import { AppRoutingModule } from "./app-routing.module";
import { HomeModule } from "./home/home.module";
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { NgbDateDayjsAdapter } from "./config/datepicker-adapter";
import { fontAwesomeIcons } from "./config/font-awesome-icons";
import { httpInterceptorProviders } from "app/core/interceptor/index";
import { MainComponent } from "./layouts/main/main.component";
import { NavbarComponent } from "./layouts/navbar/navbar.component";
import { FooterComponent } from "./layouts/footer/footer.component";
import { PageRibbonComponent } from "./layouts/profiles/page-ribbon.component";
import { ErrorComponent } from "./layouts/error/error.component";
import { MatIconModule } from "@angular/material/icon";
// import { NgxPaginationModule } from 'ngx-pagination';
// import { NgSelectModule } from '@ng-select/ng-select';
// import { Ng2SearchPipeModule } from 'ng2-search-filter';
import { Ng2GoogleChartsModule } from "ng2-google-charts";
import { NgApexchartsModule } from "ng-apexcharts";
import { GraphQLModule } from "./graphql.module";
// import { ApolloAngularModule} from 'apollo-angular';
// import { HttpLinkModule } from 'apollo-angular/http';
import { ListMaterialModule } from "./entities/list-material/list-material.module";
import { IdleService } from "./entities/list-material/services/idle.service";
export function initIdle(idle: IdleService) {
  return () => {};
}

@NgModule({
  declarations: [
    MainComponent,
    NavbarComponent,
    ErrorComponent,
    PageRibbonComponent,
    FooterComponent,
  ],
  bootstrap: [MainComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    SharedModule,
    HomeModule,
    AppRoutingModule,
    ReactiveFormsModule,
    ListMaterialModule,
    EntityRoutingModule,
    Ng2GoogleChartsModule,
    MatIconModule,
    NgApexchartsModule,
    HttpClientModule,
    NgMultiSelectDropDownModule,
    MatMenuModule,
    // HttpLinkModule,
    GraphQLModule,
    // ApolloAngularModule,
    NgxPaginationModule,
    NgxWebstorageModule.forRoot(),
    ServiceWorkerModule.register("ngsw-worker.js", { enabled: false }),
    // jhipster-needle-angular-add-module JHipster will add new module here
    // Set this to true to enable service worker (PWA)
    // NgxPaginationModule,
    // NgSelectModule,
    // NgMultiSelectDropDownModule,
    // Ng2SearchPipeModule,
  ],
  providers: [
    Title,
    { provide: LOCALE_ID, useValue: "en" },
    { provide: NgbDateAdapter, useClass: NgbDateDayjsAdapter },
    httpInterceptorProviders,
    IdleService,
    {
      provide: APP_INITIALIZER,
      useFactory: initIdle,
      deps: [IdleService],
      multi: true,
    },
  ],
})
export class AppModule {
  constructor(
    applicationConfigService: ApplicationConfigService,
    iconLibrary: FaIconLibrary,
    dpConfig: NgbDatepickerConfig,
  ) {
    applicationConfigService.setEndpointPrefix(SERVER_API_URL);
    registerLocaleData(locale);
    iconLibrary.addIcons(...fontAwesomeIcons);
    dpConfig.minDate = {
      year: dayjs().subtract(100, "year").year(),
      month: 1,
      day: 1,
    };
  }
}
