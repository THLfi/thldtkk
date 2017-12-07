import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { Title } from '@angular/platform-browser'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
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

  constructor(
    private editorStudyService: EditorStudyService,
    private route: ActivatedRoute,
    private titleService: Title,
    private breadcrumbService: BreadcrumbService,
    private langPipe: LangPipe) {
      this.sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES
    }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadingStudy = true
      this.study = null

      this.editorStudyService.getStudy(params['id']).subscribe(study => {
        this.study = study
        this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(study)
        this.updatePageTitle()

        this.loadingStudy = false
      })
    })
  }

  private updatePageTitle():void {
    if(this.study.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
