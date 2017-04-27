import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DataSetListComponent } from './views/data-set/data-set-list.component';
import { DataSetComponent } from './views/data-set/data-set.component';
import { DataSetEditComponent } from "./views/data-set/data-set-edit.component";
import { InstanceVariableEditComponent } from "./views/data-set/instance-variable-edit.component";

const routes: Routes = [
    {
        path: 'datasets/:dataSetId/instanceVariables/:instanceVariableId/edit',
        component: InstanceVariableEditComponent,
    },
    {
        path: 'datasets/:id/edit',
        component: DataSetEditComponent,
    },
    {
        path: 'datasets/:id',
        component: DataSetComponent,

    },
    {
        path: 'datasets',
        component: DataSetListComponent
    },
    {
        path: '',
        redirectTo: '/datasets',
        pathMatch: 'full'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes, { useHash: true })],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
