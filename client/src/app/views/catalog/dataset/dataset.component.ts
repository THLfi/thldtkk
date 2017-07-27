import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser'
import { LangPipe } from '../../../utils/lang.pipe'

import { Dataset } from '../../../model2/dataset';
import { DatasetService } from '../../../services2/dataset.service';

@Component({
  templateUrl: './dataset.component.html',
  styleUrls: ['./dataset.component.css']
})
export class DatasetComponent implements OnInit {

  dataset: Dataset;
  language: string

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

  private getDataSet() {
    const datasetId = this.route.snapshot.params['id'];
    this.datasetService.getDataset(datasetId)
      .subscribe(dataset => {
        this.dataset = dataset
        this.updatePageTitle()
      })
  }

  private updatePageTitle():void {
    if(this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
