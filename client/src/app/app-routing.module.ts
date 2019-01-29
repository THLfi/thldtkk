import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageIdentifier } from './utils/page-identifier'

import { CatalogFrontPageComponent } from "./views/catalog/frontpage/catalog-front-page.component"
import { CatalogStudyListComponent } from './views/catalog/study/catalog-study-list.component'
import { CatalogStudyViewComponent } from './views/catalog/study/catalog-study-view.component'
import { DatasetComponent } from "./views/catalog/dataset/dataset.component";

import { CodeListListComponent } from './views/editor/code-list/code-list-list.component'
import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component';
import { DataSetEditComponent } from "./views/editor/dataset/data-set-edit.component";
import { PersonListComponent } from "./views/editor/person/person-list.component";
import { EditorStudyListComponent } from './views/editor/study/editor-study-list.component'
import { EditorStudyDatasetsComponent } from './views/editor/study/editor-study-datasets.component'
import { EditorStudyViewComponent } from './views/editor/study/editor-study-view.component'
import { IndexComponent } from './index.component'
import { IndexRedirectGuard } from './index-redirect-guard'
import { EditorLoginComponent } from './views/editor/login/editor-login.component'
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";
import { InstanceVariableEditComponent } from "./views/editor/dataset/instance-variable-edit.component";
import { InstanceVariableSearchComponent } from "./views/catalog/instancevariables/instance-variable-search.component";
import { EditorInstanceVariableSearchComponent } from "./views/editor/instancevariable/editor-instancevariable-search.component";
import { EditorVariableViewComponent } from "./views/editor/variable/editor-variable-view.component";
import { InstanceVariableViewComponent } from "./views/editor/dataset/instance-variable-view.component";
import { OrganizationListComponent } from './views/editor/organization/organization-list.component'
import { RequireLoginGuard } from './require-login-guard'
import { StudyEditComponent } from './views/editor/study/study-edit.component'

import { StudyAdministrativeEditComponent } from './views/editor/study/study-administrative-edit.component'
import { StudyAdministrativeViewComponent } from './views/editor/study/study-administrative-view.component'
import { StudyGroupViewComponent} from './views/catalog/studygroup/studygroup-view.component'
import { UnitTypeListComponent } from './views/editor/unittype/unit-type-list.component'
import { UniverseListComponent } from "./views/editor/universe/universe-list.component"
import { VariableListComponent } from "./views/editor/variable/variable-list.component"
import { VariableViewComponent} from './views/catalog/variable/variable-view.component'

const routes: Routes = [
    {
        path:'catalog',
        component: CatalogFrontPageComponent,
        data: {title:'pageTitles.catalog.frontpage', pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/studies/:id',
        component: CatalogStudyViewComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/studies/:studyId/datasets/:datasetId',
        component: DatasetComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/studies/:studyId/datasets/:datasetId/instanceVariables/:instanceVariableId',
        component: InstanceVariableComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/studies',
        component: CatalogStudyListComponent,
        data: {title:'pageTitles.catalog.studies',pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/variables/:id',
        component: VariableViewComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/instancevariables',
        component: InstanceVariableSearchComponent,
        data: {title:'pageTitles.catalog.instanceVariableSearch',pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/studygroups/:id',
        component: StudyGroupViewComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },

    {
        path: 'editor',
        data: {
          pageType: PageIdentifier.EDITOR,
        },
        canActivateChild: [
          RequireLoginGuard
        ],
        children: [
          {
            path: 'codelists',
            component: CodeListListComponent,
            data: {
              title: 'pageTitles.editor.codeLists',
              hasSidebar: false
            }
          },
          {
            path: 'variables',
            component: VariableListComponent,
            data: {
              title:'pageTitles.editor.variables'
            }
          },
          {
            path: 'universes',
            component: UniverseListComponent,
            data: {
              title:'pageTitles.editor.universes'
            }
          },
          {
            path: 'unitTypes',
            component: UnitTypeListComponent,
            data: {
              title:'pageTitles.editor.unitTypeList'
            }
          },
          {
            path: 'organizations',
            component: OrganizationListComponent,
            data: {
              title:'pageTitles.editor.organizationsAndOrganizationUnits'
            }
          },
          {
            path: 'persons',
            component: PersonListComponent,
            data: {
              title:'pageTitles.editor.personList'
            }
          },
          {
            path: 'studies',
            component: EditorStudyListComponent,
            data: {
              title:'pageTitles.editor.studies'
            }
          },
          {
            path: 'studies/new',
            component: StudyEditComponent,
            data: {
              title: 'pageTitles.editor.newStudy',
              hasSidebar: true
            }
          },
          {
            path: 'studies/:id/edit',
            component: StudyEditComponent,
            data: {
              title: 'pageTitles.editor.editStudy',
              hasSidebar: true
            }
          },
          {
            path: 'studies/:id',
            component: EditorStudyViewComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/administrative-information',
            component: StudyAdministrativeViewComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/edit-administrative-information',
            component: StudyAdministrativeEditComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:id/datasets',
            component: EditorStudyDatasetsComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/new',
            component: DataSetEditComponent,
            data: {
              title: 'pageTitles.editor.newDataset',
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/:datasetId',
            component: EditorDataSetComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/:datasetId/edit',
            component: DataSetEditComponent,
            data: {
              title: 'pageTitles.editor.editDataset',
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/:datasetId/instanceVariables/new',
            component: InstanceVariableEditComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/:datasetId/instanceVariables/:instanceVariableId',
            component: InstanceVariableViewComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/:datasetId/instanceVariables/:instanceVariableId/edit',
            component: InstanceVariableEditComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'studies/:studyId/datasets/:datasetId/instanceVariables',
            component: DatasetInstanceVariablesViewComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: '',
            redirectTo: 'studies',
            pathMatch: 'full'
          },
          {
            path: 'variables/:id',
            component: EditorVariableViewComponent
          },
          {
            path: 'instancevariables',
            component: EditorInstanceVariableSearchComponent,
            data: {
              title: 'pageTitles.editor.instanceVariableSearch'
            }
          }
        ]
    },
    {
        path: 'login',
        component: EditorLoginComponent,
        data: {
          title:'pageTitles.editor.frontpageLogin',
          hideNavBar: true,
          pageType: PageIdentifier.EDITOR
        }
    },
    {
      path: '',
      // Just a dummy component that is not actually ever used. User will
      // always be redirected to either catalog or editor by IndexRedirectGuard.
      component: IndexComponent,
      pathMatch: 'full',
      canActivate: [IndexRedirectGuard]
    }
];

@NgModule({
    declarations: [
      IndexComponent
    ],
    providers: [
      IndexRedirectGuard
    ],
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
