import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { LangPipe } from '../../../utils/lang.pipe'
import { TranslateService } from "@ngx-translate/core"
import { Title } from '@angular/platform-browser'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { Dataset } from '../../../model2/dataset'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { Observable } from 'rxjs'
import { Study } from '../../../model2/study'
import { StudySidebarActiveSection } from '../study/sidebar/study-sidebar-active-section'

@Component({
  templateUrl: './dataset-view.component.html'
})
export class DatasetViewComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES

  constructor(
    private studyService: EditorStudyService,
    private datasetService: EditorDatasetService,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title,
    private langPipe: LangPipe,
    private breadcrumbService: BreadcrumbService
  ) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.dataset = null
      Observable.forkJoin(
        this.studyService.getStudy(params['studyId']),
        this.datasetService.getDataset(params['studyId'], params['datasetId'])
      ).subscribe(data => {
        this.study = data[0]
        this.dataset = data[1]
        this.updatePageTitle()
        this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset)
      })
    })
  }

  private updatePageTitle():void {
    if (this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

}
