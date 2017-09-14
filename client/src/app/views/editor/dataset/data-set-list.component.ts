import { Component, OnInit } from '@angular/core'

import { Dataset } from '../../../model2/dataset'
import { DatasetService3 } from '../../../services3/dataset.service'

@Component({
    templateUrl: './data-set-list.component.html'
})
export class DatasetListComponent implements OnInit {

  datasets: Dataset[] = []

  constructor(
    private dataSetService: DatasetService3
  ) { }

  ngOnInit(): void {
    this.dataSetService.getAll()
      .subscribe(dataSets => this.datasets = dataSets)
  }

}
