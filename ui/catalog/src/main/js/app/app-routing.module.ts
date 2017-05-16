import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DatasetListComponent } from './views/data-set/dataset-list.component';
import { DataSetComponent } from './views/data-set/dataset.component';

const routes: Routes = [
  {
    path: 'datasets/:id',
    component: DataSetComponent,
  },
  {
    path: 'datasets',
    component: DatasetListComponent
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
export class AppRoutingModule { }
