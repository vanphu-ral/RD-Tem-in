import { registerLocaleData } from "@angular/common";
import localeVi from "@angular/common/locales/vi";

registerLocaleData(localeVi);
import("./bootstrap").catch((err) => console.error(err));
