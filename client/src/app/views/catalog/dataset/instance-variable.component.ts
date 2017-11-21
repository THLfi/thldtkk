import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'

import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableReferencePeriod } from './instance-variable-reference-period'
import { LangPipe } from '../../../utils/lang.pipe'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { Study } from '../../../model2/study'

@Component({
  templateUrl: './instance-variable.component.html',
  styleUrls: [ './instance-variable.component.css' ]
})
export class InstanceVariableComponent implements OnInit {

  study: Study
  dataset: Dataset
  instanceVariable: InstanceVariable
  language: string

  referencePeriod: InstanceVariableReferencePeriod

  constructor(
    private studyService: PublicStudyService,
    private datasetService: PublicDatasetService,
    private instanceVariableService: PublicInstanceVariableService,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private langPipe: LangPipe,
    private titleService: Title
  ) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.route.params.subscribe(params =>
      this.updateInstanceVariable(
        params['studyId'],
        params['datasetId'],
        params['instanceVariableId']
      ))
  }

  private updateInstanceVariable(studyId: string, datasetId: string, instanceVariableId: string) {
    Observable.forkJoin(
      this.studyService.getStudy(studyId),
      this.datasetService.getDataset(studyId, datasetId),
      this.instanceVariableService.getInstanceVariable(studyId, datasetId, instanceVariableId)
    ).subscribe(data => {
      this.study = data[0]
      this.dataset = data[1]
      this.instanceVariable = data[2]
      this.updatePageTitle()
      this.updateReferencePeriod()
    })
  }

  private updatePageTitle():void {
    if (this.instanceVariable.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.instanceVariable.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

  private updateReferencePeriod() {
    this.referencePeriod = new InstanceVariableReferencePeriod(this.study, this.dataset, this.instanceVariable)
  }

}
