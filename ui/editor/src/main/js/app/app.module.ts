import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule, Http } from '@angular/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { DatepickerModule } from 'ngx-bootstrap/datepicker';
import { DatePipe } from '@angular/common';

import { environment } from "../environments/environment";

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { DataSetListComponent } from './views/data-set/data-set-list.component';
import { DataSetComponent } from './views/data-set/data-set.component';
import { DataSetEditComponent } from "./views/data-set/data-set-edit.component";
import { DataSetService } from "./services/data-set.service";
import { InstanceVariableEditComponent } from "./views/data-set/instance-variable-edit.component";
import { InstanceVariableService } from './services/instance-variable.service';
import { IsoDatePicker } from "./views/common/iso-datepicker.component";
import { LifecyclePhaseComponent } from "./views/common/lifecycle-phase.component";
import { LifecyclePhaseService } from "./services/lifecycle-phase.service";
import { NodeUtils } from "./utils/node-utils";
import { OrganizationComponent } from "./views/common/organization.component";
import { OrganizationService } from "./services/organization.service";
import { OrganizationUnitComponent } from "./views/common/organization-unit.component";
import { OrganizationUnitService } from "./services/organization-unit.service";
import { PersonComponent } from "./views/common/person.component";
import { PersonService } from "./services/person.service";
import { PersonInRoleService } from "./services/person-in-role.service";
import { PersonInRoleComponent } from "./views/common/person-in-role.component";
import { PopulationService } from "./services/population.service";
import { PropertyValueComponent } from './views/common/property-value.component';
import { PopulationComponent } from "./views/common/population.component";
import { RoleService } from "./services/role.service";
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
        InstanceVariableEditComponent,
        IsoDatePicker,
        OrganizationComponent,
        OrganizationUnitComponent,
        PersonComponent,
        PersonInRoleComponent,
        PopulationComponent,
        PropertyValueComponent,
        LifecyclePhaseComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        AppRoutingModule,
        DatepickerModule.forRoot(),
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
        OrganizationUnitService,
        PersonService,
        PersonInRoleService,
        PopulationService,
        RoleService,
        UsageConditionService,
        LifecyclePhaseService,
        DatePipe
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
