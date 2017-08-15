import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { Title } from '@angular/platform-browser'
import { LangPipe } from '../../../utils/lang.pipe'

import { Dataset } from "../../../model2/dataset";
import { DatasetService } from "../../../services2/dataset.service";
import { SidebarActiveSection } from './sidebar/sidebar-active-section'

@Component({
  templateUrl: './dataset-view.component.html',
  styleUrls: [ './dataset-view.component.css' ]
})
export class DatasetViewComponent implements OnInit {

  dataset: Dataset
  language: string

  sidebarActiveSection = SidebarActiveSection.DATASET

  constructor(private datasetService: DatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getDataSet();
  }

  getDataSet() {
    const datasetId = this.route.snapshot.params['id'];
    this.datasetService.getDataset(datasetId)
      .subscribe(dataset => {
        this.dataset = dataset
        this.updatePageTitle()
      })
  }

  updatePageTitle():void {
    if(this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

  confirmPublish(): void {
    this.translateService.get('confirmPublishDataset')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.datasetService.publishDataSet(this.dataset)
            .subscribe(dataSet => this.dataset = dataSet)
        }
      })
  }

  confirmUnpublish(): void {
    this.translateService.get('confirmUnpublishDataset')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.datasetService.unpublishDataSet(this.dataset)
            .subscribe(dataSet => this.dataset = dataSet)
        }
      })
  }

}
