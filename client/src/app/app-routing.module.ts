import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageIdentifier } from './utils/page-identifier'

import { CatalogFrontPageComponent } from "./views/catalog/frontpage/catalog-front-page.component"
import { DatasetComponent } from "./views/catalog/dataset/dataset.component";
import { DatasetListComponent } from "./views/catalog/dataset/dataset-list.component";

import { DatasetInstanceVariablesViewComponent } from './views/editor/dataset/dataset-instance-variables-view.component'
import { DatasetListComponent as EditorDataSetListComponent } from './views/editor/dataset/data-set-list.component'
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component';
import { DataSetEditComponent } from "./views/editor/dataset/data-set-edit.component";
import { IndexComponent } from './index.component'
import { IndexRedirectGuard } from './index-redirect-guard'
import { EditorLoginComponent } from './views/editor/login/editor-login.component'
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";
import { InstanceVariableEditComponent } from "./views/editor/dataset/instance-variable-edit.component";
import { InstanceVariableSearchComponent } from "./views/catalog/instancevariables/instance-variable-search.component";
import { InstanceVariableViewComponent } from "./views/editor/dataset/instance-variable-view.component";
import { RequireLoginGuard } from './require-login-guard'
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
            path: 'datasets/:datasetId/instanceVariables/new',
            component: InstanceVariableEditComponent,
            data: {
              title: 'pageTitles.editor.newInstanceVariable',
              hasSidebar: true
            }
          },
          {
            path: 'datasets/:datasetId/instanceVariables/:instanceVariableId/edit',
            component: InstanceVariableEditComponent,
            data: {
              title: 'pageTitles.editor.editInstanceVariable',
              hasSidebar: true
            }
          },
          {
            path: 'datasets/:datasetId/instanceVariables/:instanceVariableId',
            component: InstanceVariableViewComponent,
            data: {
              pageType: PageIdentifier.EDITOR,
              hasSidebar: true
            }
          },
          {
            path: 'datasets/:datasetId/instanceVariables',
            component: DatasetInstanceVariablesViewComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'datasets/new',
            component: DataSetEditComponent,
            data: {
              title: 'pageTitles.editor.newDataset',
              hasSidebar: true
            }
          },
          {
            path: 'datasets/:id/edit',
            component: DataSetEditComponent,
            data: {
              title: 'pageTitles.editor.editDataset',
              hasSidebar: true
            }
          },
          {
            path: 'datasets/:id',
            component: EditorDataSetComponent,
            data: {
              hasSidebar: true
            }
          },
          {
            path: 'datasets',
            component: EditorDataSetListComponent,
            data: {
              title:'pageTitles.editor.datasetList'
            }
          },
          {
            path: 'variables',
            component: VariableListComponent,
            data: {
              title:'pageTitles.editor.variableList'
            }
          },
          {
            path: 'universes',
            component: UniverseListComponent,
            data: {
              title:'pageTitles.editor.universeList'
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
            path: '',
            redirectTo: 'datasets',
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
