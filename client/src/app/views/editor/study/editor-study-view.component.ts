import { ActivatedRoute } from '@angular/router'
import { Component } from '@angular/core'
import { TranslateService } from "@ngx-translate/core"
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'
import { LangPipe  } from '../../../utils/lang.pipe'
import { Title } from '@angular/platform-browser'
import { Study } from '../../../model2/study'

@Component({
  templateUrl:'./editor-study-view.component.html',
  styleUrls: ['./editor-study-view.component.css']
})

export class EditorStudyViewComponent {

  study: Study
  loadingStudy: boolean
  sidebarActiveSection: StudySidebarActiveSection
  language: string

  constructor(
    private editorStudyService: EditorStudyService,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title,
    private langPipe: LangPipe) {
      this.sidebarActiveSection = StudySidebarActiveSection.STUDY
      this.language = this.translateService.currentLang
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

  confirmPublish(): void {
    this.translateService.get('study.confirmPublish')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.editorStudyService.publish(this.study)
            .subscribe(study => this.study = study)
        }
    })
  }

  confirmWithdraw(): void {
    this.translateService.get('study.confirmWithdraw')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.editorStudyService.withdraw(this.study)
            .subscribe(study => this.study = study)
        }
    })
  }

}