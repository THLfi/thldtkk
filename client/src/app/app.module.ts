import { AutoCompleteModule } from 'primeng/components/autocomplete/autocomplete';
import { BrowserModule, Title } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChipsModule } from 'primeng/components/chips/chips';
import { NgModule } from '@angular/core'
import { FormsModule } from '@angular/forms'
import { HttpModule, Http } from '@angular/http'
import { TooltipModule } from 'primeng/components/tooltip/tooltip';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core'
import { TranslateHttpLoader } from '@ngx-translate/http-loader'
import { DatePipe } from '@angular/common'
import { DialogModule } from "primeng/components/dialog/dialog";
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { MultiSelectModule } from 'primeng/primeng';
import { TruncateModule } from 'ng2-truncate';
import { TruncateCharactersPipe } from 'ng2-truncate/dist/truncate-characters.pipe'
import { OverlayPanelModule } from 'primeng/components/overlaypanel/overlaypanel';
import { GrowlModule } from 'primeng/components/growl/growl'
import { CalendarModule } from 'primeng/components/calendar/calendar'

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
import { InstanceQuestionService } from './services2/instance-question.service'
import { LifecyclePhaseService } from './services2/lifecycle-phase.service'
import { UnitService } from './services2/unit.service'
import { OrganizationService} from './services2/organization.service'
import { OrganizationUnitService } from './services2/organization-unit.service'
import { PopulationService } from './services2/population.service'
import { QuantityService } from "./services2/quantity.service";
import { VariableService } from './services2/variable.service'
import { UnitTypeService } from './services2/unit-type.service'
import { UsageConditionService } from './services2/usage-condition.service'

// common components
import { AutogrowTextarea } from './views/common/autogrow-textarea.directive'
import { DateUtils } from './utils/date-utils'
import { LangPipe } from './utils/lang.pipe'
import { LoadingSpinner } from './views/common/loading-spinner.component'
import { RequiredFieldIndicator } from './views/common/required-field-indicator.component'
import { ViewCodeListCodeItemsModalComponent } from './views/editor/dataset/view-code-list-code-items-modal.component'

// catalog components (few renamed to avoid name clash with editor)
import { DatasetComponent } from './views/catalog/dataset/dataset.component'
import { DatasetListComponent } from './views/catalog/dataset/dataset-list.component'
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";
import { InstanceVariableSearchComponent } from './views/catalog/instancevariables/instance-variable-search.component'
import { InstanceVariableSearchResultComponent } from './views/catalog/instancevariables/instance-variable-search-result.component'
import { CatalogFrontPageComponent } from './views/catalog/frontpage/catalog-front-page.component'

// editor components
import { CodeListEditModalComponent } from './views/editor/dataset/code-list-edit-modal.component'
import { DataSetEditComponent } from './views/editor/dataset/data-set-edit.component'
import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DataSetListComponent as EditorDataSetListComponent } from './views/editor/dataset/data-set-list.component'
import { DatasetSidebarComponent } from './views/editor/dataset/sidebar/dataset-sidebar.component'
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component'
import { InstanceQuestionEditModalComponent } from './views/editor/dataset/instance-question-modal.component'
import { InstanceVariableEditComponent } from './views/editor/dataset/instance-variable-edit.component'
import { InstanceVariableViewComponent } from './views/editor/dataset/instance-variable-view.component';
import { InstanceVariablesImportModalComponent } from './views/editor/dataset/instance-variables-import-modal.component'
import { QuantityEditModalComponent } from './views/editor/dataset/quantity-edit-modal.component'
import { UnitEditModalComponent } from './views/editor/dataset/unit-edit-modal.component'
import { UnitTypeEditModalComponent } from './views/editor/dataset/unit-type-edit-modal.component'
import { UniverseEditModalComponent } from './views/editor/dataset/universe-edit-modal.component'
import { UniverseService } from './services2/universe.service'
import { VariableEditModalComponent } from './views/editor/dataset/variable-edit-modal.component'
import { VariableModalComponent } from './views/editor/variable/variable-modal.component'
import { VariableSearchComponent } from './views/editor/variable/variable-search.component'
import { VariableSearchResultComponent } from './views/editor/variable/variable-search-result.component'

export function TranslateHttpLoaderFactory(http: Http) {
  return new TranslateHttpLoader(http, environment.contextPath + '/assets/i18n/', '.json')
}

@NgModule({
    declarations: [
        AppComponent,
        AutogrowTextarea,
        DatasetComponent,
        DataSetEditComponent,
        DatasetInstanceVariablesViewComponent,
        DatasetListComponent,
        DatasetSidebarComponent,
        EditorDataSetListComponent,
        EditorDataSetComponent,
        InstanceVariableComponent,
        InstanceVariableEditComponent,
        InstanceVariableViewComponent,
        InstanceVariableSearchComponent,
        InstanceVariableSearchResultComponent,
        LangPipe,
        LoadingSpinner,
        RequiredFieldIndicator,
        UnitTypeEditModalComponent,
        UniverseEditModalComponent,
        VariableEditModalComponent,
        QuantityEditModalComponent,
        UnitEditModalComponent,
        CodeListEditModalComponent,
        ViewCodeListCodeItemsModalComponent,
        InstanceVariablesImportModalComponent,
        InstanceQuestionEditModalComponent,
        CatalogFrontPageComponent,
        VariableModalComponent,
        VariableSearchComponent,
        VariableSearchResultComponent
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
        CalendarModule,
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
        InstanceQuestionService,
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
        GrowlMessageService,
        Title,
        PopulationService,
        UniverseService,
        TruncateCharactersPipe,
        DateUtils
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
