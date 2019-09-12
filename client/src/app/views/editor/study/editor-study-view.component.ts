
import {finalize} from 'rxjs/operators';
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
import {ConfirmationService} from 'primeng/primeng'

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
  public publishAction: boolean = false

  constructor(
    private editorStudyService: EditorStudyService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService,
    private titleService: Title,
    private breadcrumbService: BreadcrumbService,
    private langPipe: LangPipe,
    private confirmationService: ConfirmationService,
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
      this.publishAction = true;
      this.translateService.get('study.confirmPublish').subscribe(confirmationMessage => {
          this.confirmationService.confirm({
            message: confirmationMessage,
            accept: () => {
                this.editorStudyService.publish(this.study)
                .subscribe(study => this.study = study)
              }
            })
          })
  }

  confirmWithdraw(): void {
      this.publishAction = true;
      this.translateService.get('study.confirmWithdraw').subscribe(confirmationMessage => {
          this.confirmationService.confirm({
            message: confirmationMessage,
            accept: () => {
                this.editorStudyService.withdraw(this.study)
                .subscribe(study => this.study = study)
              }
            })
          })
  }

  confirmReissue(): void {
      this.publishAction = true;
      this.translateService.get('study.confirmReissue').subscribe(confirmationMessage => {
          this.confirmationService.confirm({
            message: confirmationMessage,
            accept: () => {
                this.editorStudyService.reissue(this.study)
                .subscribe(study => this.study = study)
              }
            })
          })
  }

  confirmRemove(): void {
    this.publishAction = false;
    this.translateService.get('study.confirmRemove').subscribe(confirmationMessage => {
    this.confirmationService.confirm({
      message: confirmationMessage,
      accept: () => {
        this.deleteInProgress = true
        this.editorStudyService.delete(this.study.id).pipe(
        finalize(() => this.deleteInProgress = false))
          .subscribe(() => this.router.navigate(['/editor/studies']))
        }
      })
    })
  }

}
