import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'
import { Observable } from 'rxjs'

import { Dataset } from '../../../model2/dataset'
import { LangPipe } from '../../../utils/lang.pipe'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'
import { Study } from '../../../model2/study'

@Component({
  templateUrl: './dataset.component.html',
  styleUrls: ['./dataset.component.css']
})
export class DatasetComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  constructor(
    private studyService: PublicStudyService,
    private datasetService: PublicDatasetService,
    private instanceVariableService: PublicInstanceVariableService,
    private langPipe: LangPipe,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title
  ) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.updateDataset(params['studyId'], params['datasetId'])
    })
  }

  private updateDataset(studyId: string, datasetId: string): void {
    this.study = null
    this.dataset = null

    Observable.forkJoin(
      this.studyService.getStudy(studyId),
      this.datasetService.getDataset(studyId, datasetId)
    ).subscribe(data => {
      this.study = data[0]
      this.dataset = data[1]
      this.updatePageTitle()
    })
  }

  private updatePageTitle(): void {
    if (this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

  composeInstanceVariableExportUrl(): string {
    return this.instanceVariableService.getInstanceVariableAsCsvExportPath(this.dataset.id)
  }

}
