import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'
import { LangPipe  } from '../../../utils/lang.pipe'
import { Title } from '@angular/platform-browser'
import { Study } from '../../../model2/study'
import { Organization } from '../../../model2/organization'
import { User } from '../../../model2/user'


@Component({
  templateUrl: './editor-study-datasets.component.html'
})

export class EditorStudyDatasetsComponent implements OnInit {

  study: Study
  loadingStudy: boolean
  sidebarActiveSection: StudySidebarActiveSection
  organizations: Organization[]
  user: User

  constructor(
    private editorStudyService: EditorStudyService,
    private userService: CurrentUserService,
    private route: ActivatedRoute,
    private titleService: Title,
    private langPipe: LangPipe) {
      this.sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES
    }

  ngOnInit() {
    const studyId = this.route.snapshot.params['id']
    this.loadingStudy = true
    this.editorStudyService.getStudy(studyId).subscribe(study => {
      this.study = study
      this.updatePageTitle()
      this.userService.getUserOrganizations()
        .subscribe(organizations => this.organizations = organizations)
      this.userService.getCurrentUserObservable()
        .subscribe(user => this.user = user)

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