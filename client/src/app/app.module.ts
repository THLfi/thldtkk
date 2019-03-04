import { AutoCompleteModule } from 'primeng/components/autocomplete/autocomplete';
import { BrowserModule, Title } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CheckboxModule } from 'primeng/primeng'
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
import { PapaParseModule } from 'ngx-papaparse';
import { SweetAlert2Module } from '@toverux/ngx-sweetalert2'

import { AppComponent } from './app.component'
import { AppRoutingModule } from './app-routing.module'

import { environment } from '../environments/environment'
import {SecurityPrincipleService} from "./services-common/security-principle.service";

// utils
import { NodeUtils } from './utils/node-utils'

// services
import { BreadcrumbService } from './services-common/breadcrumb.service'
import { CodeListService3 } from './services-common/code-list.service'
import { CommonErrorHandlingHttpService } from './services-common/common-error-handling-http.service'
import { ConceptService } from './services-common/concept.service'
import { EditorDatasetService } from './services-editor/editor-dataset.service'
import { EditorSystemService } from './services-editor/editor-system.service'
import { EditorSystemRoleService } from './services-editor/editor-system-role.service'
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
import { PublicStudyService } from './services-public/public-study.service'
import { PublicInstanceVariableService } from './services-public/public-instance-variable.service'
import { QuantityService } from './services-common/quantity.service'
import { RoleService } from './services-common/role.service'
import { EditorStudyService } from './services-editor/editor-study.service'
import { StudyGroupService } from './services-common/study-group.service'
import { UnitService } from './services-common/unit.service'
import { UnitTypeService } from './services-common/unit-type.service'
import { UniverseService } from './services-common/universe.service'
import { UsageConditionService } from './services-common/usage-condition.service'
import { VariableService } from './services-common/variable.service'

// common components
import { AutogrowTextarea } from './views/common/autogrow-textarea.directive'
import { DateUtils } from './utils/date-utils'
import { FontAwesomeComponent } from './views/common/font-awesome.component'
import { GlyphiconComponent } from './views/common/glyphicon.component'
import { LangPipe } from './utils/lang.pipe'
import { LoadingSpinner } from './views/common/loading-spinner.component'
import { RequiredFieldIndicator } from './views/common/required-field-indicator.component'
import { ViewCodeListCodeItemsModalComponent } from './views/editor/dataset/view-code-list-code-items-modal.component'
import { InfoTooltipComponent } from './views/common/info-tooltip.component'

// catalog components (few renamed to avoid name clash with editor)
import { CatalogStudyListComponent } from './views/catalog/study/catalog-study-list.component'
import { CatalogStudyViewComponent } from './views/catalog/study/catalog-study-view.component'
import { DatasetComponent } from './views/catalog/dataset/dataset.component'
import { DatasetListComponent } from './views/catalog/dataset/dataset-list.component'
import { IconUniverse } from './views/catalog/icons/icon-universe.component'
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";
import { InstanceVariableSearchComponent } from './views/catalog/instancevariables/instance-variable-search.component'
import { InstanceVariableSearchResultComponent } from './views/catalog/instancevariables/instance-variable-search-result.component'
import { CatalogFrontPageComponent } from './views/catalog/frontpage/catalog-front-page.component'
import { StudyGroupViewComponent} from './views/catalog/studygroup/studygroup-view.component'
import { VariableViewComponent} from './views/catalog/variable/variable-view.component'

// editor components
import { AboutEditorComponent } from './views/editor/login/about-editor.component'
import { AdminMenuComponent } from './views/editor/menu/admin-menu.component'
import { BreadcrumbComponent } from './views/editor/breadcrumb/breadcrumb.component'
import { CodeListEditModalComponent } from './views/editor/dataset/code-list-edit-modal.component'
import { CodeListListComponent } from './views/editor/code-list/code-list-list.component'
import { CurrentUserService } from './services-editor/user.service'
import { SecurityPrincipleEditModalComponent } from "./views/editor/study/security-principle-edit-modal.component";
import { StudyAdministrativeEditComponent } from './views/editor/study/study-administrative-edit.component'
import { StudyAdministrativeReadOnlyFieldsComponent } from './views/editor/study/study-administrative-read-only-fields.component'
import { StudyAdministrativeViewComponent } from './views/editor/study/study-administrative-view.component'
import { SystemEditModalComponent } from './views/editor/study/system-edit-modal.component'
import { DataSetEditComponent } from './views/editor/dataset/data-set-edit.component'
import { DatasetHeaderComponent } from './views/editor/dataset/dataset-header.component'
import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DatasetRelationsEditComponent } from './views/editor/dataset/dataset-relationships/dataset-relations-edit.component'
import { DatasetRelationTypeDropdown } from './views/editor/dataset/dataset-relationships/dataset-relation-type-dropdown.component'
import { DatasetTabsComponent } from './views/editor/dataset/dataset-tabs.component'
import { DatasetSidebarComponent } from './views/editor/dataset/sidebar/dataset-sidebar.component'
import { EditorHelpLinkComponent } from './views/editor/help/editor-help-link.component'
import { EditorHelpTextComponent } from './views/editor/help/editor-help-text.component'
import { EditorInstanceVariableSearchComponent } from './views/editor/instancevariable/editor-instancevariable-search.component'
import { EditorInstanceVariableSearchResultComponent} from './views/editor/instancevariable/editor-instancevariable-search-result.component'
import { EditorVariableViewComponent } from './views/editor/variable/editor-variable-view.component'
import { EditorStudyListComponent } from './views/editor/study/editor-study-list.component'
import { StudyEditComponent } from './views/editor/study/study-edit.component'
import { EditorStudyDatasetsComponent} from './views/editor/study/editor-study-datasets.component'
import { EditorStudyViewComponent} from './views/editor/study/editor-study-view.component'
import { StudyGroupEditModalComponent } from './views/editor/study-group/study-group-edit-modal.component'
import { DatasetViewComponent as EditorDatasetComponent } from './views/editor/dataset/dataset-view.component'
import { EditorLoginComponent } from './views/editor/login/editor-login.component'
import { InstanceQuestionEditModalComponent } from './views/editor/dataset/instance-question-modal.component'
import { InstanceVariableEditComponent } from './views/editor/dataset/instance-variable-edit.component'
import { InstanceVariableViewComponent } from './views/editor/dataset/instance-variable-view.component';
import { InstanceVariablesImportModalComponent } from './views/editor/dataset/instance-variables-import-modal.component'
import { LastModifiedComponent } from './views/editor/dataset/last-modified.component'
import { LoginAdviceComponent } from './views/editor/login/login-advice.component'
import { LogoutMessageComponent } from './views/editor/login/logout-message.component'
import { OrganizationDropdownComponent } from './views/common/organization-dropdown.component'
import { OrganizationConsistencyValidator } from './views/editor/study/validation/organization-consistency.validator'
import { OrganizationUnitEditModalComponent} from "./views/editor/dataset/organization-unit-edit-modal.component";
import { OrganizationListComponent } from './views/editor/organization/organization-list.component'
import { OrganizationEditModalComponent} from "./views/editor/study/organization-edit-modal.component";
import { PersonEditModalComponent } from './views/editor/dataset/person-edit-modal.component'
import { PersonListComponent } from "./views/editor/person/person-list.component";
import { QuantityEditModalComponent } from './views/editor/dataset/quantity-edit-modal.component'
import { RequireLoginGuard } from './require-login-guard'
import { SidebarIconAdministrativeInfo } from './views/editor/study/sidebar/sidebar-icon-administrative-info.component'
import { SidebarIconDatasets } from './views/editor/study/sidebar/sidebar-icon-datasets.component'
import { SidebarIconStudy } from './views/editor/study/sidebar/sidebar-icon-study.component'
import { StudyRelationsEditComponent } from './views/editor/study/study-relationships/study-relations-edit.component'
import { StudyRelationTypeDropdown } from './views/editor/study/study-relationships/study-relation-type-dropdown.component'
import { StudySidebarComponent } from './views/editor/study/sidebar/study-sidebar.component'
import { SystemConsistencyValidator } from './views/editor/study/validation/system-consistency.validator'
import { UnitEditModalComponent } from './views/editor/dataset/unit-edit-modal.component'
import { UnitTypeEditModalComponent } from './views/editor/dataset/unit-type-edit-modal.component'
import { UnitTypeListComponent } from './views/editor/unittype/unit-type-list.component'
import { UniverseEditModalComponent } from './views/editor/dataset/universe-edit-modal.component'
import { UniverseListComponent } from './views/editor/universe/universe-list.component'
import { UserMenuComponent } from './views/editor/menu/user-menu.component'
import { VariableEditModalComponent } from './views/editor/dataset/variable-edit-modal.component'
import { VariableListComponent } from './views/editor/variable/variable-list.component'
import { VariableModalComponent } from './views/editor/variable/variable-modal.component'
import {StudyAdministrativeEditPrincipleOfProtectionFieldsComponent} from 'app/views/editor/study/study-administrative-edit-principle-of-protection-fields.component';

export function TranslateHttpLoaderFactory(http: Http) {
  return new TranslateHttpLoader(http, environment.contextPath + '/assets/i18n/', '.json')
}

@NgModule({
    declarations: [
        AboutEditorComponent,
        AppComponent,
        AutogrowTextarea,
        BreadcrumbComponent,
        CatalogStudyListComponent,
        CatalogStudyViewComponent,
        CodeListListComponent,
        SidebarIconAdministrativeInfo,
        SidebarIconDatasets,
        SidebarIconStudy,
        StudyAdministrativeEditComponent,
        StudyAdministrativeReadOnlyFieldsComponent,
        StudyAdministrativeEditPrincipleOfProtectionFieldsComponent,
        StudyAdministrativeViewComponent,
        DatasetComponent,
        DatasetHeaderComponent,
        DataSetEditComponent,
        DatasetInstanceVariablesViewComponent,
        DatasetListComponent,
        DatasetRelationsEditComponent,
        DatasetRelationTypeDropdown,
        DatasetSidebarComponent,
        DatasetTabsComponent,
        EditorDatasetComponent,
        EditorHelpLinkComponent,
        EditorHelpTextComponent,
        EditorInstanceVariableSearchComponent,
        EditorInstanceVariableSearchResultComponent,
        EditorVariableViewComponent,
        EditorLoginComponent,
        EditorStudyListComponent,
        EditorStudyDatasetsComponent,
        EditorStudyViewComponent,
        FontAwesomeComponent,
        GlyphiconComponent,
        InfoTooltipComponent,
        IconUniverse,
        InstanceVariableComponent,
        InstanceVariableEditComponent,
        InstanceVariableViewComponent,
        InstanceVariableSearchComponent,
        InstanceVariableSearchResultComponent,
        LangPipe,
        LastModifiedComponent,
        LoadingSpinner,
        LoginAdviceComponent,
        LogoutMessageComponent,
        OrganizationConsistencyValidator,
        OrganizationUnitEditModalComponent,
        OrganizationListComponent,
        OrganizationEditModalComponent,
        PersonEditModalComponent,
        PersonListComponent,
        RequiredFieldIndicator,
        StudyEditComponent,
        StudySidebarComponent,
        StudyRelationsEditComponent,
        StudyRelationTypeDropdown,
        StudyGroupEditModalComponent,
        SystemEditModalComponent,
        SystemConsistencyValidator,
        OrganizationDropdownComponent,
        UnitTypeEditModalComponent,
        UnitTypeListComponent,
        UniverseEditModalComponent,
        VariableEditModalComponent,
        QuantityEditModalComponent,
        UnitEditModalComponent,
        UserMenuComponent,
        AdminMenuComponent,
        CodeListEditModalComponent,
        ViewCodeListCodeItemsModalComponent,
        InstanceVariablesImportModalComponent,
        InstanceQuestionEditModalComponent,
        CatalogFrontPageComponent,
        UniverseListComponent,
        VariableListComponent,
        VariableModalComponent,
        StudyGroupViewComponent,
        VariableViewComponent,
        SecurityPrincipleEditModalComponent
    ],
    imports: [
        CheckboxModule,
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
        PapaParseModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: TranslateHttpLoaderFactory,
                deps: [Http]
            }
        }),
        SweetAlert2Module.forRoot({
            buttonsStyling: false,
            customClass: 'modal-content',
            confirmButtonClass: 'btn btn-primary',
            cancelButtonClass: 'btn'
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
        CodeListService3,
        ConceptService,
        EditorDatasetService,
        EditorSystemRoleService,
        EditorSystemService,
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
        PublicStudyService,
        QuantityService,
        RoleService,
        EditorStudyService,
        UnitService,
        UnitTypeService,
        UniverseService,
        UsageConditionService,
        VariableService,
        BreadcrumbService,
        StudyGroupService,
        SecurityPrincipleService,
        { provide: Http, useClass: CommonErrorHandlingHttpService }
    ],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
