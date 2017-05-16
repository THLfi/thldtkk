import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";

import { Dataset } from "../../../model2/dataset";
import { DatasetService } from "../../../services2/dataset.service";

@Component({
  templateUrl: './dataset-view.component.html',
  styleUrls: [ './dataset-view.component.css' ]
})
export class DatasetViewComponent implements OnInit {

  dataset: Dataset;

  constructor(private datasetService: DatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService) {
  }

  ngOnInit() {
    this.getDataSet();
  }

  private getDataSet() {
    const datasetId = this.route.snapshot.params['id'];
    this.datasetService.getDataset(datasetId)
      .subscribe(dataset => this.dataset = dataset)
  }
/*
  confirmPublish(): void {
    this.translateService.get('confirmPublishDataset')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.datasetService.publishDataSet(this.dataSet)
            .subscribe(dataSet => this.dataSet = dataSet)
        }
      })
  }

  confirmUnpublish(): void {
    this.translateService.get('confirmUnpublishDataset')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.dataSetService.unpublishDataSet(this.dataSet)
            .subscribe(dataSet => this.dataSet = dataSet)
        }
      })
  }
*/
}
