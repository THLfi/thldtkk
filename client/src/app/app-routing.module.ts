import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DataSetListComponent as CatalogDataSetListComponent } from './views/catalog/data-set/data-set-list.component';
import { DataSetComponent as CatalogDataSetComponent } from './views/catalog/data-set/data-set.component';

import { DataSetListComponent as EditorDataSetListComponent } from './views/editor/data-set/data-set-list.component';
import { DataSetComponent as EditorDataSetComponent } from './views/editor/data-set/data-set.component';
import { DataSetEditComponent } from "./views/editor/data-set/data-set-edit.component";
import { InstanceVariableEditComponent } from "./views/editor/data-set/instance-variable-edit.component";

const routes: Routes = [
    {
        path: 'catalog/datasets/:id',
        component: CatalogDataSetComponent,
    },
    {
        path: 'catalog/datasets',
        component: CatalogDataSetListComponent
    },
    {
        path: 'catalog',
        redirectTo: 'catalog/datasets',
    },
    {
        path: 'editor/datasets/:dataSetId/instanceVariables/new',
        component: InstanceVariableEditComponent,
    },
    {
        path: 'editor/datasets/:dataSetId/instanceVariables/:instanceVariableId/edit',
        component: InstanceVariableEditComponent,
    },
    {
        path: 'editor/datasets/new',
        component: DataSetEditComponent,
    },
    {
        path: 'editor/datasets/:id/edit',
        component: DataSetEditComponent,
    },
    {
        path: 'editor/datasets/:id',
        component: EditorDataSetComponent,

    },
    {
        path: 'editor/datasets',
        component: EditorDataSetListComponent
    },
    {
        path: 'editor',
        redirectTo: 'editor/datasets',
    },
    {
        path: '',
        redirectTo: 'catalog/datasets',
        pathMatch: 'full'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
