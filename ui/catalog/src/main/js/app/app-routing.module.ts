import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BrowseDataSetsComponent } from './views/browse-data-sets/browse-data-sets.component';
import { ViewDataSetComponent } from './views/view-data-set/view-data-set.component';

const routes: Routes = [
    {
        path: 'datasets/:id',
        component: ViewDataSetComponent,

    },
    {
        path: 'datasets',
        component: BrowseDataSetsComponent
    },
    {
        path: '',
        redirectTo: '/datasets',
        pathMatch: 'full'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
