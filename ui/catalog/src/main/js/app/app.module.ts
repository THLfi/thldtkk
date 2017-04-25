import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { DataSetListComponent } from './views/data-set/data-set-list.component';
import { DataSetComponent } from './views/data-set/data-set.component';
import { DataSetService } from "./services/data-set.service";
import { OrganizationComponent } from "./views/common/organization.component";
import { OrganizationService } from "./services/organization.service";
import { PropertyValueComponent } from './views/common/property-value.component';

@NgModule({
    declarations: [
        AppComponent,
        DataSetListComponent,
        DataSetComponent,
        OrganizationComponent,
        PropertyValueComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        AppRoutingModule
    ],
    providers: [
        DataSetService,
        OrganizationService
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
