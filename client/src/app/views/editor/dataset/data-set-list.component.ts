import { Component, OnInit } from '@angular/core'

import { Dataset } from '../../../model2/dataset'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'

@Component({
    templateUrl: './data-set-list.component.html',
    styleUrls: ['./data-set-list.component.css']
})
export class DatasetListComponent implements OnInit {

  datasets: Dataset[] = []
  isLoadingDatasets: boolean = false

  constructor(
    private dataSetService: EditorDatasetService
  ) { }

  ngOnInit(): void {
    this.isLoadingDatasets = true;
    this.dataSetService.getAll()
      .subscribe(dataSets => {
        this.datasets = dataSets
        this.isLoadingDatasets = false
      })
  }

}
