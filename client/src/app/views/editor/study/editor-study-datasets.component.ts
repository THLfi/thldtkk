
import {finalize} from 'rxjs/operators';
import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { Title } from '@angular/platform-browser'
import { TranslateService } from '@ngx-translate/core'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { LangPipe  } from '../../../utils/lang.pipe'
import { Study } from '../../../model2/study'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'

@Component({
  templateUrl: './editor-study-datasets.component.html'
})
export class EditorStudyDatasetsComponent implements OnInit {

  study: Study
  loadingStudy: boolean
  sidebarActiveSection: StudySidebarActiveSection

  deleteInProgress: boolean = false

  constructor(
    private editorStudyService: EditorStudyService,
    private editorDatasetService: EditorDatasetService,
    private route: ActivatedRoute,
    private titleService: Title,
    private breadcrumbService: BreadcrumbService,
    private langPipe: LangPipe,
    private translateService: TranslateService,
    public currentUserService: CurrentUserService
  ) {
    this.sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES
  }

  ngOnInit() {
    this.route.params.subscribe(params => this.getStudy(params['id']))
  }

  private getStudy(studyId: string) {
    this.loadingStudy = true
    this.study = null

    this.editorStudyService.getStudy(studyId).subscribe(study => {
      this.study = study
      this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(study)
      this.updatePageTitle()

      this.loadingStudy = false
    })

  }

  private updatePageTitle():void {
    if(this.study.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

  confirmRemoveDataset(event: any, datasetId: string): void {
    event.stopPropagation()

    this.translateService.get('confirmDatasetDelete')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.deleteInProgress = true

          this.editorDatasetService.delete(this.study.id, datasetId).pipe(
            finalize(() => {
              this.deleteInProgress = false
            }))
            .subscribe(() => this.getStudy(this.study.id))
        }
      })
  }

}
