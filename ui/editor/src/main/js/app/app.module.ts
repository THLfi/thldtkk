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
import { DataSetEditComponent } from "./views/data-set/data-set-edit.component";
import { DataSetService } from "./services/data-set.service";
import { InstanceVariableComponent } from './views/data-set/instance-variable.component';
import { InstanceVariableEditComponent } from "./views/data-set/instance-variable-edit.component";
import { InstanceVariableService } from './services/instance-variable.service';
import { NodeUtils } from "./utils/node-utils";
import { OrganizationComponent } from "./views/common/organization.component";
import { OrganizationService } from "./services/organization.service";
import { PopulationService } from "./services/population.service";
import { PropertyValueComponent } from './views/common/property-value.component';
import { PopulationComponent } from "./views/common/population.component";
import { UsageConditionService } from "./services/usage-condition.service";

export function TranslateHttpLoaderFactory(http: Http) {
  return new TranslateHttpLoader(http, environment.contextPath + '/assets/i18n/', '.json');
}

@NgModule({
    declarations: [
        AppComponent,
        DataSetListComponent,
        DataSetComponent,
        DataSetEditComponent,
        InstanceVariableComponent,
        InstanceVariableEditComponent,
        OrganizationComponent,
        PopulationComponent,
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
        InstanceVariableService,
        NodeUtils,
        OrganizationService,
        PopulationService,
        UsageConditionService
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
