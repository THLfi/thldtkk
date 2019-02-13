import { ActivatedRoute, Router } from '@angular/router'
import { Component } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { LangPipe  } from '../../../utils/lang.pipe'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'
import { Study } from '../../../model2/study'
import { Title } from '@angular/platform-browser'

@Component({
  templateUrl:'./editor-study-view.component.html',
  styleUrls: ['./editor-study-view.component.css']
})
export class EditorStudyViewComponent {

  study: Study
  loadingStudy: boolean
  sidebarActiveSection: StudySidebarActiveSection
  language: string

  deleteInProgress: boolean = false

  constructor(
    private editorStudyService: EditorStudyService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService,
    private titleService: Title,
    private breadcrumbService: BreadcrumbService,
    private langPipe: LangPipe,
    public currentUserService: CurrentUserService
  ) {
      this.sidebarActiveSection = StudySidebarActiveSection.STUDY
      this.language = this.translateService.currentLang
    }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadingStudy = true
      this.study = null

      this.editorStudyService.getStudy(params['id'], false).subscribe(study => {
        this.study = study
        this.updatePageTitle()
        this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(study)
        this.loadingStudy = false
      })
    })
  }

  private updatePageTitle() {
    if (this.study.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
      let bareTitle:string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
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

  confirmReissue(): void {
    this.translateService.get('study.confirmReissue')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.editorStudyService.reissue(this.study)
            .subscribe(study => this.study = study)
        }
    })
  }

  confirmRemove(): void {
    this.translateService.get('study.confirmRemove')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.deleteInProgress = true

          this.editorStudyService.delete(this.study.id)
            .finally(() => this.deleteInProgress = false)
            .subscribe(() => this.router.navigate(['/editor/studies']))
        }
      })
  }

}
