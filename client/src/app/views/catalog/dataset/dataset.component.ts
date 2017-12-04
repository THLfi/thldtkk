import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'
import { Observable } from 'rxjs'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableReferencePeriod } from './instance-variable-reference-period'
import { LangPipe } from '../../../utils/lang.pipe'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'
import { Study } from '../../../model2/study'

class InstanceVariableWrapper {

  constructor(
    public instanceVariable: InstanceVariable,
    public referencePeriod: InstanceVariableReferencePeriod
  ) { }

}

@Component({
  templateUrl: './dataset.component.html',
  styleUrls: ['./dataset.component.css']
})
export class DatasetComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  referencePeriodStart: string
  referencePeriodEnd: string
  referencePeriodInheritedFromStudy: boolean

  wrappedInstanceVariables: InstanceVariableWrapper[] = []

  constructor(
    private studyService: PublicStudyService,
    private datasetService: PublicDatasetService,
    private instanceVariableService: PublicInstanceVariableService,
    private breadcrumbService: BreadcrumbService,
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
    this.wrappedInstanceVariables = []

    Observable.forkJoin(
      this.studyService.getStudy(studyId),
      this.datasetService.getDataset(studyId, datasetId)
    ).subscribe(data => {
      this.study = data[0]
      this.dataset = data[1]
      this.updatePageTitle()
      this.breadcrumbService.updateCatalogBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset)
      this.updateReferencePeriod()
      this.updateWrappedInstanceVariables()
    })
  }

  private updatePageTitle(): void {
    if (this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

  private updateReferencePeriod() {
    this.referencePeriodInheritedFromStudy = false

    if (this.dataset.referencePeriodStart || this.dataset.referencePeriodEnd) {
      this.referencePeriodStart = this.dataset.referencePeriodStart
      this.referencePeriodEnd = this.dataset.referencePeriodEnd
    }
    else if (this.study.referencePeriodStart || this.study.referencePeriodEnd) {
      this.referencePeriodStart = this.study.referencePeriodStart
      this.referencePeriodEnd = this.study.referencePeriodEnd
      this.referencePeriodInheritedFromStudy = true
    }
    else {
      this.referencePeriodStart = null
      this.referencePeriodEnd = null
    }
  }

  updateWrappedInstanceVariables() {
    let wrappers: InstanceVariableWrapper[] = []
    this.dataset.instanceVariables
      .forEach(iv => {
        const referencePeriod = new InstanceVariableReferencePeriod(this.study, this.dataset, iv)
        wrappers = [ ...wrappers, new InstanceVariableWrapper(iv, referencePeriod) ]
      })
    this.wrappedInstanceVariables = wrappers
  }

  composeInstanceVariableExportUrl(): string {
    return this.instanceVariableService.getInstanceVariableAsCsvExportPath(this.study.id, this.dataset.id)
  }

}
