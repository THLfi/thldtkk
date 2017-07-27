import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageIdentifier } from './utils/page-identifier'

import { CatalogFrontPageComponent } from "./views/catalog/frontpage/catalog-front-page.component"
import { DatasetComponent } from "./views/catalog/dataset/dataset.component";
import { DatasetListComponent } from "./views/catalog/dataset/dataset-list.component";

import { DataSetListComponent as EditorDataSetListComponent } from './views/editor/dataset/data-set-list.component';
import { DatasetViewComponent as EditorDataSetComponent } from './views/editor/dataset/dataset-view.component';
import { DataSetEditComponent } from "./views/editor/dataset/data-set-edit.component";
import { InstanceVariableComponent } from "./views/catalog/dataset/instance-variable.component";
import { InstanceVariableEditComponent } from "./views/editor/dataset/instance-variable-edit.component";
import { InstanceVariableViewComponent } from "./views/editor/dataset/instance-variable-view.component";

import { InstanceVariableSearchComponent } from "./views/catalog/instancevariables/instance-variable-search.component";

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
        path: 'catalog/instancevariables',
        component: InstanceVariableSearchComponent,
        data: {title:'pageTitles.catalog.instanceVariableSearch',pageType:PageIdentifier.CATALOG}
    },
    {
        path: 'editor/datasets/:datasetId/instanceVariables/new',
        component: InstanceVariableEditComponent,
        data: {title:'pageTitles.editor.newInstanceVariable', pageType:PageIdentifier.EDITOR}
    },
    {
        path: 'editor/datasets/:datasetId/instanceVariables/:instanceVariableId/edit',
        component: InstanceVariableEditComponent,
        data: {title:'pageTitles.editor.editInstanceVariable',pageType:PageIdentifier.EDITOR}
    },
    {
        path: 'editor/datasets/:datasetId/instanceVariables/:instanceVariableId',
        component: InstanceVariableViewComponent,
        data: {pageType:PageIdentifier.EDITOR}
    },
    {
        path: 'editor/datasets/new',
        component: DataSetEditComponent,
        data: {title:'pageTitles.editor.newDataset',pageType:PageIdentifier.EDITOR}
    },
    {
        path: 'editor/datasets/:id/edit',
        component: DataSetEditComponent,
        data: {title:'pageTitles.editor.editDataset',pageType:PageIdentifier.EDITOR}
    },
    {
        path: 'editor/datasets/:id',
        component: EditorDataSetComponent,
        data: {pageType:PageIdentifier.EDITOR}

    },
    {
        path: 'editor/datasets',
        component: EditorDataSetListComponent,
        data: {title:'pageTitles.editor.datasetList',pageType:PageIdentifier.EDITOR}
    },
    {
        path: 'editor',
        redirectTo: 'editor/datasets',
        data: {title:'pageTitles.editor.datasetList',pageType:PageIdentifier.EDITOR}
    },
    {
        path: '',
        redirectTo: 'catalog',
        data: {pageType:PageIdentifier.CATALOG},
        pathMatch: 'full'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
