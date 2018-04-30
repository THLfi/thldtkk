import { ActivatedRoute, Router, NavigationEnd } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { Title } from '@angular/platform-browser'
import { TranslateService } from '@ngx-translate/core'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { Dataset } from '../../../model2/dataset'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { LangPipe } from '../../../utils/lang.pipe'
import { Study } from '../../../model2/study'
import { StudySidebarActiveSection } from '../study/sidebar/study-sidebar-active-section'

@Component({
  templateUrl: './instance-variable-view.component.html'
})
export class InstanceVariableViewComponent implements OnInit {

  study: Study
  dataset: Dataset
  instanceVariable: InstanceVariable
  language: string

  readonly sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES

  deleteInProgress: boolean = false

  constructor(
    private studyService: EditorStudyService,
    private datasetService: EditorDatasetService,
    private instanceVariableService: EditorInstanceVariableService,
    private breadcrumbService: BreadcrumbService,
    private titleService: Title,
    private langPipe: LangPipe,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService
  ) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getInstanceVariable()

    this.router.routeReuseStrategy.shouldReuseRoute = function(){
      return false;
    }

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.router.navigated = false;
        window.scrollTo(0, 0);
      }
    })
  }

  private getInstanceVariable() {
    const studyId = this.route.snapshot.params['studyId']
    const datasetId = this.route.snapshot.params['datasetId']
    const instanceVariableId = this.route.snapshot.params['instanceVariableId']

    Observable.forkJoin(
      this.studyService.getStudy(studyId),
      this.datasetService.getDataset(studyId, datasetId),
      this.instanceVariableService.getInstanceVariable(studyId, datasetId, instanceVariableId)
    ).subscribe(data => {
      this.study = data[0]
      this.dataset = data[1]
      this.instanceVariable = data[2]
      this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset, this.instanceVariable)
      this.updatePageTitle()

    })
  }

  private updatePageTitle(): void {
    if (this.instanceVariable.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.instanceVariable.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

  confirmRemove(): void {
    this.translateService.get('confirmInstanceVariableDelete')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.deleteInProgress = true

          this.instanceVariableService.deleteInstanceVariable(this.study.id, this.dataset.id, this.instanceVariable.id)
            .finally(() => {
              this.deleteInProgress = false
            })
            .subscribe(() => this.router.navigate(
              ['/editor/studies', this.study.id, 'datasets', this.dataset.id, 'instanceVariables']))
        }
      })
  }

  goToNextInstanceVariable(): void {
    this.instanceVariableService.getNextInstanceVariableId(this.study.id, this.dataset.id, this.instanceVariable.id)
      .subscribe(instanceVariableId => {
        this.router.navigate([
          '/editor/studies',
          this.study.id,
          'datasets',
          this.dataset.id,
          'instanceVariables',
          instanceVariableId])
      })
  }
}
