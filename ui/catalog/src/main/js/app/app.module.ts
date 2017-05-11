import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule, Http } from '@angular/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { environment } from "../environments/environment";

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { DataSetListComponent } from './views/data-set/data-set-list.component';
import { DataSetComponent } from './views/data-set/data-set.component';
import { DataSetService } from "./services/data-set.service";
import { InstanceVariableComponent } from './views/data-set/instance-variable.component';
import { InstanceVariableService } from './services/instance-variable.service';
import { OrganizationComponent } from "./views/common/organization.component";
import { OrganizationService } from "./services/organization.service";
import { PersonComponent } from './views/common/person.component';
import { PersonService } from "./services/person.service";
import { PropertyValueComponent } from './views/common/property-value.component';
import { RoleService } from "./services/role.service";

export function TranslateHttpLoaderFactory(http: Http) {
  return new TranslateHttpLoader(http, environment.contextPath + '/assets/i18n/', '.json');
}

@NgModule({
    declarations: [
        AppComponent,
        DataSetListComponent,
        DataSetComponent,
        InstanceVariableComponent,
        OrganizationComponent,
        PersonComponent,
        PropertyValueComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        AppRoutingModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: TranslateHttpLoaderFactory,
                deps: [Http]
            }
        })
    ],
    providers: [
        DataSetService,
        OrganizationService,
        PersonService,
        RoleService,
        InstanceVariableService
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
