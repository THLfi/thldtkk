import { BrowserModule } from '@angular/platform-browser'
import { NgModule } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { HttpModule, Http } from '@angular/http'
import { TranslateModule, TranslateLoader } from '@ngx-translate/core'
import { TranslateHttpLoader } from '@ngx-translate/http-loader'
import { DatepickerModule } from 'ngx-bootstrap/datepicker'
import { DatePipe } from '@angular/common'

import { AppComponent } from './app.component'
import { AppRoutingModule } from './app-routing.module'

import { environment } from '../environments/environment'

// utils
import { NodeUtils } from './utils/node-utils'

// services
import { DatasetService } from './services2/dataset.service'
import { OrganizationService} from './services2/organization.service'
import { OrganizationUnitService } from './services2/organization-unit.service'
import { InstanceVariableService } from './services2/instance-variable.service'
import { UsageConditionService } from './services2/usage-condition.service'
import { LifecyclePhaseService } from './services2/lifecycle-phase.service'
import { DatasetTypeService } from './services2/dataset-type.service'

// common components
import { AutogrowTextarea } from './views/common/autogrow-textarea.directive'
import { IsoDatePicker } from './views/common/iso-datepicker.component'
import { LangPipe } from './utils/lang.pipe'

// catalog components (few renamed to avoid name clash with editor)
import { DatasetComponent } from './views/catalog/dataset/dataset.component'
import { DatasetListComponent } from './views/catalog/dataset/dataset-list.component'

// editor components
import { DataSetListComponent as EditorDataSetListComponent } from './views/editor/dataset/data-set-list.component'
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component'
import { DataSetEditComponent } from './views/editor/dataset/data-set-edit.component'
import { InstanceVariableEditComponent } from './views/editor/dataset/instance-variable-edit.component'

export function TranslateHttpLoaderFactory(http: Http) {
  return new TranslateHttpLoader(http, environment.contextPath + '/assets/i18n/', '.json')
}

@NgModule({
    declarations: [
        AppComponent,
        AutogrowTextarea,
        DatasetComponent,
        DataSetEditComponent,
        DatasetListComponent,
        EditorDataSetListComponent,
        EditorDataSetComponent,
        InstanceVariableEditComponent,
        IsoDatePicker,
        LangPipe
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
        DatasetService,
        InstanceVariableService,
        NodeUtils,
        OrganizationService,
        OrganizationUnitService,
        UsageConditionService,
        LifecyclePhaseService,
        DatasetTypeService,
        DatePipe
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
