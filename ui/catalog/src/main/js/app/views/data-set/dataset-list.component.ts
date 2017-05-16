import { Component, OnInit } from '@angular/core';
import 'rxjs/add/operator/toPromise';

import { Dataset } from "../../model2/dataset";
import { DatasetService } from "../../services2/dataset.service";

@Component({
  templateUrl: './dataset-list.component.html'
})
export class DatasetListComponent implements OnInit {

  datasets: Dataset[]

  constructor(
    private datasetService: DatasetService
  ) { }

  ngOnInit(): void {
    this.datasetService.getAllDatasets()
      .subscribe(datasets => {
        this.datasets = datasets.filter(ds => ds.isPublic == true)
      })
  }

}
