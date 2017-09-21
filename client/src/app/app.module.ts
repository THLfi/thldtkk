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
import { CodeListService3 } from './services-common/code-list.service'
import { CommonErrorHandlingHttpService } from './services-common/common-error-handling-http.service'
import { ConceptService } from './services-common/concept.service'
import { EditorDatasetService } from './services-editor/editor-dataset.service'
import { DatasetTypeService } from './services-common/dataset-type.service'
import { GrowlMessageService } from './services-common/growl-message.service'
import { InstanceQuestionService } from './services-common/instance-question.service'
import { EditorInstanceVariableService } from './services-editor/editor-instance-variable.service'
import { LifecyclePhaseService } from './services-common/lifecycle-phase.service'
import { OrganizationService } from './services-common/organization.service'
import { OrganizationUnitService } from './services-common/organization-unit.service'
import { PersonService } from './services-common/person.service'
import { PopulationService } from './services-common/population.service'
import { PublicDatasetService } from './services-public/public-dataset.service'
import { PublicInstanceVariableService } from './services-public/public-instance-variable.service'
import { QuantityService } from './services-common/quantity.service'
import { RoleService } from './services-common/role.service'
import { UnitService } from './services-common/unit.service'
import { UnitTypeService } from './services-common/unit-type.service'
import { UniverseService } from './services-common/universe.service'
import { UsageConditionService } from './services-common/usage-condition.service'
import { VariableService } from './services-common/variable.service'

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
import { CurrentUserService } from './services-editor/user.service'
import { DataSetEditComponent } from './views/editor/dataset/data-set-edit.component'
import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DatasetListComponent as EditorDatasetListComponent } from './views/editor/dataset/data-set-list.component'
import { DatasetSidebarComponent } from './views/editor/dataset/sidebar/dataset-sidebar.component'
import { DatasetViewComponent as EditorDatasetComponent } from './views/editor/dataset/dataset-view.component'
import { InstanceQuestionEditModalComponent } from './views/editor/dataset/instance-question-modal.component'
import { InstanceVariableEditComponent } from './views/editor/dataset/instance-variable-edit.component'
import { InstanceVariableViewComponent } from './views/editor/dataset/instance-variable-view.component';
import { InstanceVariablesImportModalComponent } from './views/editor/dataset/instance-variables-import-modal.component'
import { LoginComponent } from './views/editor/login.component'
import { PersonEditModalComponent } from './views/editor/dataset/person-edit-modal.component'
import { QuantityEditModalComponent } from './views/editor/dataset/quantity-edit-modal.component'
import { RequireLoginGuard } from './require-login-guard'
import { UnitEditModalComponent } from './views/editor/dataset/unit-edit-modal.component'
import { UnitTypeEditModalComponent } from './views/editor/dataset/unit-type-edit-modal.component'
import { UnitTypeListComponent } from './views/editor/unittype/unit-type-list.component'
import { UniverseEditModalComponent } from './views/editor/dataset/universe-edit-modal.component'
import { UserMenuComponent } from './views/editor/menu/user-menu.component'
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
        LoginComponent,
        PersonEditModalComponent,
        RequiredFieldIndicator,
        UnitTypeEditModalComponent,
        UnitTypeListComponent,
        UniverseEditModalComponent,
        VariableEditModalComponent,
        QuantityEditModalComponent,
        UnitEditModalComponent,
        UserMenuComponent,
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
        NodeUtils,
        DatePipe,
        LangPipe,
        GrowlMessageService,
        Title,
        TruncateCharactersPipe,
        DateUtils,
        CurrentUserService,
        ConfirmationService,
        RequireLoginGuard,
        // Version 3 services
        CodeListService3,
        ConceptService,
        EditorDatasetService,
        DatasetTypeService,
        InstanceQuestionService,
        EditorInstanceVariableService,
        LifecyclePhaseService,
        OrganizationService,
        OrganizationUnitService,
        PersonService,
        PopulationService,
        PublicDatasetService,
        PublicInstanceVariableService,
        QuantityService,
        RoleService,
        UnitService,
        UnitTypeService,
        UniverseService,
        UsageConditionService,
        VariableService,
        { provide: Http, useClass: CommonErrorHandlingHttpService }
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
