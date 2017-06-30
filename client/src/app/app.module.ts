import { AutoCompleteModule } from 'primeng/components/autocomplete/autocomplete';
import { BrowserModule } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChipsModule } from 'primeng/components/chips/chips';
import { NgModule } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { HttpModule, Http } from '@angular/http'
import { TooltipModule } from 'primeng/components/tooltip/tooltip';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core'
import { TranslateHttpLoader } from '@ngx-translate/http-loader'
import { DatepickerModule } from 'ngx-bootstrap/datepicker'
import { DatePipe } from '@angular/common'
import { DialogModule } from "primeng/components/dialog/dialog";
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { MultiSelectModule } from 'primeng/primeng';
import { TruncateModule } from 'ng2-truncate';
import { OverlayPanelModule } from 'primeng/components/overlaypanel/overlaypanel';
import { GrowlModule } from 'primeng/components/growl/growl'

import { AppComponent } from './app.component'
import { AppRoutingModule } from './app-routing.module'

import { environment } from '../environments/environment'

// utils
import { NodeUtils } from './utils/node-utils'

// services
import { CodeListService } from "./services2/code-list.service";
import { ConceptService } from './services2/concept.service'
import { DatasetService } from './services2/dataset.service'
import { DatasetTypeService } from './services2/dataset-type.service'
import { GrowlMessageService } from './services2/growl-message.service'
import { InstanceVariableService } from './services2/instance-variable.service'
import { LifecyclePhaseService } from './services2/lifecycle-phase.service'
import { UnitService } from './services2/unit.service'
import { OrganizationService} from './services2/organization.service'
import { OrganizationUnitService } from './services2/organization-unit.service'
import { QuantityService } from "./services2/quantity.service";
import { VariableService } from './services2/variable.service'
import { UnitTypeService } from './services2/unit-type.service'
import { UsageConditionService } from './services2/usage-condition.service'

// common components
import { AutogrowTextarea } from './views/common/autogrow-textarea.directive'
import { IsoDatePicker } from './views/common/iso-datepicker.component'
import { LangPipe } from './utils/lang.pipe'
import { LoadingSpinner } from './views/common/loading-spinner.component'
import { RequiredFieldIndicator } from './views/common/required-field-indicator.component'
import { ViewCodeListCodeItemsModalComponent } from './views/editor/dataset/view-code-list-code-items-modal.component'

// catalog components (few renamed to avoid name clash with editor)
import { DatasetComponent } from './views/catalog/dataset/dataset.component'
import { DatasetListComponent } from './views/catalog/dataset/dataset-list.component'
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";

// editor components
import { DataSetListComponent as EditorDataSetListComponent } from './views/editor/dataset/data-set-list.component'
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component'
import { DataSetEditComponent } from './views/editor/dataset/data-set-edit.component'
import { InstanceVariableEditComponent } from './views/editor/dataset/instance-variable-edit.component'
import { InstanceVariableViewComponent } from './views/editor/dataset/instance-variable-view.component';
import { QuantityEditModalComponent } from "./views/editor/dataset/quantity-edit-modal.component";
import { UnitEditModalComponent } from './views/editor/dataset/unit-edit-modal.component'
import { VariableEditModalComponent } from './views/editor/dataset/variable-edit-modal.component';

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
        InstanceVariableComponent,
        InstanceVariableEditComponent,
        InstanceVariableViewComponent,
        IsoDatePicker,
        LangPipe,
        LoadingSpinner,
        RequiredFieldIndicator,
        VariableEditModalComponent,
        QuantityEditModalComponent,
        UnitEditModalComponent,
        ViewCodeListCodeItemsModalComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        HttpModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        AutoCompleteModule,
        TooltipModule,
        ChipsModule,
        MultiSelectModule,
        DropdownModule,
        DialogModule,
        TruncateModule,
        OverlayPanelModule,
        GrowlModule,
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
        VariableService,
        NodeUtils,
        OrganizationService,
        OrganizationUnitService,
        UnitTypeService,
        UsageConditionService,
        LifecyclePhaseService,
        DatasetTypeService,
        DatePipe,
        ConceptService,
        UnitService,
        QuantityService,
        LangPipe,
        CodeListService,
        GrowlMessageService
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
