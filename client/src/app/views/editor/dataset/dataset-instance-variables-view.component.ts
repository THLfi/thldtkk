import { OnInit, Component } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { Observable } from 'rxjs'
import { Title } from '@angular/platform-browser'
import { TranslateService } from '@ngx-translate/core'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { Dataset } from '../../../model2/dataset'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { LangPipe } from '../../../utils/lang.pipe'
import { Study } from '../../../model2/study'
import { StudySidebarActiveSection } from '../study/sidebar/study-sidebar-active-section'

@Component({
  templateUrl: './dataset-instance-variables-view.component.html'
})
export class DatasetInstanceVariablesViewComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  studyForImportInstanceVariablesModal: Study
  datasetForImportInstanceVariablesModal: Dataset

  readonly sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES

  constructor(
    private studyService: EditorStudyService,
    private datasetService: EditorDatasetService,
    private instanceVariableService: EditorInstanceVariableService,
    private breadcrumbService: BreadcrumbService,
    private langPipe: LangPipe,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title
  ) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getStudyAndDataset()
  }

  private getStudyAndDataset() {
    const studyId = this.route.snapshot.params['studyId']
    const datasetId = this.route.snapshot.params['datasetId']
    Observable.forkJoin(
      this.studyService.getStudy(studyId),
      this.datasetService.getDataset(studyId, datasetId)
    ).subscribe(data => {
      this.study = data[0]
      this.dataset = data[1]
      this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset)
      this.updatePageTitle()
    })
  }

  private updatePageTitle():void {
    if (this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

  showImportInstanceVariablesModal() {
    this.studyForImportInstanceVariablesModal = this.study
    this.datasetForImportInstanceVariablesModal = this.dataset
  }

  closeImportInstanceVariablesModal(): void {
    this.datasetForImportInstanceVariablesModal = null
  }

  composeInstanceVariableExportUrl(): string {
    return this.instanceVariableService.getInstanceVariableAsCsvExportPath(this.study.id, this.dataset.id)
  }

}
