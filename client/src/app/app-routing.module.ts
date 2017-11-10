import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageIdentifier } from './utils/page-identifier'

import { CatalogFrontPageComponent } from "./views/catalog/frontpage/catalog-front-page.component"
import { DatasetComponent } from "./views/catalog/dataset/dataset.component";
import { DatasetListComponent } from "./views/catalog/dataset/dataset-list.component";

import { CodeListListComponent } from './views/editor/code-list/code-list-list.component'
import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component';
import { DataSetEditComponent } from "./views/editor/dataset/data-set-edit.component";
import { EditorStudyListComponent } from './views/editor/study/editor-study-list.component'
import { EditorStudyDatasetsComponent } from './views/editor/study/editor-study-datasets.component'
import { EditorStudyViewComponent } from './views/editor/study/editor-study-view.component'
import { IndexComponent } from './index.component'
import { IndexRedirectGuard } from './index-redirect-guard'
import { EditorLoginComponent } from './views/editor/login/editor-login.component'
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";
import { InstanceVariableEditComponent } from "./views/editor/dataset/instance-variable-edit.component";
import { InstanceVariableSearchComponent } from "./views/catalog/instancevariables/instance-variable-search.component";
import { InstanceVariableViewComponent } from "./views/editor/dataset/instance-variable-view.component";
import { RequireLoginGuard } from './require-login-guard'
import { StudyEditComponent } from './views/editor/study/study-edit.component'
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
        path: 'catalog/datasets/:datasetId/instanceVariables/:instanceVariableId',
        component: InstanceVariableComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/datasets/:id',
        component: DatasetComponent,
        data: {pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'catalog/datasets',
        component: DatasetListComponent,
        data: {title:'pageTitles.catalog.datasetList',pageType:PageIdentifier.CATALOG}
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
