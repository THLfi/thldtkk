import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser'
import { LangPipe } from '../../../utils/lang.pipe'

import { Dataset } from '../../../model2/dataset'
import { Study } from '../../../model2/study'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'


@Component({
  templateUrl: './dataset.component.html',
  styleUrls: ['./dataset.component.css']
})
export class DatasetComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  constructor(private datasetService: PublicDatasetService,
              private studyService: PublicStudyService,
              private instanceVariableService: PublicInstanceVariableService,
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
    const datasetId = this.route.snapshot.params['datasetId']
    const studyId = this.route.snapshot.params['studyId']

    this.datasetService.getDataset(studyId, datasetId)
      .subscribe(dataset => {
        this.dataset = dataset
        this.updatePageTitle()
      })

    this.studyService.getStudy(studyId).subscribe(study => {
      this.study = study
    })
  }

  private updatePageTitle():void {
    if(this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

  composeInstanceVariableExportUrl(): string {
    return this.instanceVariableService.getInstanceVariableAsCsvExportPath(this.dataset.id)
  } 

}
