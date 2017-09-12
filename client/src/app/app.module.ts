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
import { DataTableModule } from 'primeng/primeng'
import { SharedModule } from 'primeng/primeng'
import { ConfirmDialogModule, ConfirmationService } from 'primeng/primeng'

import { AppComponent } from './app.component'
import { AppRoutingModule } from './app-routing.module'

import { environment } from '../environments/environment'

// utils
import { NodeUtils } from './utils/node-utils'

// services
import { CodeListService } from "./services2/code-list.service";
import { CommonErrorHandlingHttpService } from './services2/common-error-handling-http.service'
import { ConceptService } from './services2/concept.service'
import { DatasetService } from './services2/dataset.service'
import { DatasetTypeService } from './services2/dataset-type.service'
import { GrowlMessageService } from './services2/growl-message.service'
import { InstanceVariableService } from './services2/instance-variable.service'
import { PublicInstanceVariableService } from './services2/public-instance-variable.service'
import { InstanceQuestionService } from './services2/instance-question.service'
import { LifecyclePhaseService } from './services2/lifecycle-phase.service'
import { UnitService } from './services2/unit.service'
import { OrganizationService} from './services2/organization.service'
import { OrganizationUnitService } from './services2/organization-unit.service'
import { PersonService } from './services2/person.service'
import { PopulationService } from './services2/population.service'
import { QuantityService } from "./services2/quantity.service";
import { RoleService } from './services2/role.service'
import { VariableService } from './services2/variable.service'
import { UnitTypeService } from './services2/unit-type.service'
import { UsageConditionService } from './services2/usage-condition.service'
// Version 3 services
import { CodeListService3 } from './services3/code-list.service'
import { ConceptService3 } from './services3/concept.service'
import { DatasetService3 } from './services3/dataset.service'
import { DatasetTypeService3 } from './services3/dataset-type.service'
import { InstanceQuestionService3 } from './services3/instance-question.service'
import { InstanceVariableService3 } from './services3/instance-variable.service'
import { LifecyclePhaseService3 } from './services3/lifecycle-phase.service'
import { OrganizationService3 } from './services3/organization.service'
import { OrganizationUnitService3 } from './services3/organization-unit.service'
import { PersonService3 } from './services3/person.service'
import { QuantityService3 } from './services3/quantity.service'
import { RoleService3 } from './services3/role.service'
import { UnitService3 } from './services3/unit.service'
import { UnitTypeService3 } from './services3/unit-type.service'
import { UniverseService3 } from './services3/universe.service'
import { UsageConditionService3 } from './services3/usage-condition.service'
import { VariableService3 } from './services3/variable.service'

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
import { VariableViewComponent} from './views/catalog/variable/variable-view.component'

// editor components
import { CodeListEditModalComponent } from './views/editor/dataset/code-list-edit-modal.component'
import { DataSetEditComponent } from './views/editor/dataset/data-set-edit.component'
import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DatasetListComponent as EditorDatasetListComponent } from './views/editor/dataset/data-set-list.component'
import { DatasetSidebarComponent } from './views/editor/dataset/sidebar/dataset-sidebar.component'
import { DatasetViewComponent as EditorDatasetComponent } from './views/editor/dataset/dataset-view.component'
import { InstanceQuestionEditModalComponent } from './views/editor/dataset/instance-question-modal.component'
import { InstanceVariableEditComponent } from './views/editor/dataset/instance-variable-edit.component'
import { InstanceVariableViewComponent } from './views/editor/dataset/instance-variable-view.component';
import { InstanceVariablesImportModalComponent } from './views/editor/dataset/instance-variables-import-modal.component'
import { PersonEditModalComponent } from './views/editor/dataset/person-edit-modal.component'
import { QuantityEditModalComponent } from './views/editor/dataset/quantity-edit-modal.component'
import { UnitEditModalComponent } from './views/editor/dataset/unit-edit-modal.component'
import { UnitTypeEditModalComponent } from './views/editor/dataset/unit-type-edit-modal.component'
import { UnitTypeListComponent } from './views/editor/unittype/unit-type-list.component'
import { UniverseEditModalComponent } from './views/editor/dataset/universe-edit-modal.component'
import { UniverseService } from './services2/universe.service'
import { UserService } from './services2/user.service'
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
        EditorDatasetListComponent,
        EditorDatasetComponent,
        InstanceVariableComponent,
        InstanceVariableEditComponent,
        InstanceVariableViewComponent,
        InstanceVariableSearchComponent,
        InstanceVariableSearchResultComponent,
        LangPipe,
        LoadingSpinner,
        PersonEditModalComponent,
        RequiredFieldIndicator,
        UnitTypeEditModalComponent,
        UnitTypeListComponent,
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
        VariableSearchResultComponent,
        VariableViewComponent
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
        DataTableModule,
        SharedModule,
        ConfirmDialogModule,
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
        PublicInstanceVariableService,
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
        DateUtils,
        PersonService,
        RoleService,
        UserService,
        ConfirmationService,
        // Version 3 services
        CodeListService3,
        ConceptService3,
        DatasetService3,
        DatasetTypeService3,
        InstanceQuestionService3,
        InstanceVariableService3,
        LifecyclePhaseService3,
        OrganizationService3,
        OrganizationUnitService3,
        PersonService3,
        QuantityService3,
        RoleService3,
        UnitService3,
        UnitTypeService3,
        UniverseService3,
        UsageConditionService3,
        VariableService3,
        { provide: Http, useClass: CommonErrorHandlingHttpService }
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
