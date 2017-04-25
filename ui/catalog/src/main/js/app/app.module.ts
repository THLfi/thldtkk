import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { BrowseDataSetsComponent } from './views/browse-data-sets/browse-data-sets.component';
import { DataSetService } from "./services/data-set.service";
import { ViewDataSetComponent } from './views/view-data-set/view-data-set.component';
import { PropertyValue } from './components/property-value.component';

@NgModule({
    declarations: [
        AppComponent,
        BrowseDataSetsComponent,
        ViewDataSetComponent,
        PropertyValue
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        AppRoutingModule
    ],
    providers: [
        DataSetService
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
