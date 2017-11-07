import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'
import { LangPipe  } from '../../../utils/lang.pipe'
import { Title } from '@angular/platform-browser'
import { Study } from '../../../model2/study'

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
    private langPipe: LangPipe) {
      this.sidebarActiveSection = StudySidebarActiveSection.DATASETS
    }

  ngOnInit() {
    const studyId = this.route.snapshot.params['id']
    this.loadingStudy = true
    this.editorStudyService.getStudy(studyId).subscribe(study => {
      this.study = study
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



}