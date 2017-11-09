import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { LangPipe  } from '../../../utils/lang.pipe'
import { Study } from '../../../model2/study'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'
import { Title } from '@angular/platform-browser'

@Component({
  templateUrl: './editor-study-datasets.component.html'
})
export class EditorStudyDatasetsComponent implements OnInit {

  study: Study
  loadingStudy: boolean
  sidebarActiveSection: StudySidebarActiveSection

  currentUserCanAddDatasets: boolean = false

  constructor(
    private editorStudyService: EditorStudyService,
    private userService: CurrentUserService,
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
        this.breadcrumbService.updateBreadcrumbsForStudyDatasetAndInstanceVariable(study)
        this.updatePageTitle()

        this.userService.getUserOrganizations()
          .subscribe(organizations => {
            const currentUserHasOrganizations: boolean = (organizations && organizations.length > 0)
            this.currentUserCanAddDatasets = this.currentUserCanAddDatasets || currentUserHasOrganizations
          })
        this.userService.isUserAdmin()
          .subscribe(isAdmin => {
            this.currentUserCanAddDatasets = this.currentUserCanAddDatasets || isAdmin
          })

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
