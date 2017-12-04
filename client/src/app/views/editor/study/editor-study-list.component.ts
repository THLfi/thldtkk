import { Component, OnInit } from '@angular/core'
import { Organization } from '../../../model2/organization'
import { Study } from '../../../model2/study'
import { User } from '../../../model2/user'
import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'

@Component({
  templateUrl: './editor-study-list.component.html',
  styleUrls: ['./editor-study-list.component.css']
})
export class EditorStudyListComponent implements OnInit {

  organizations: Organization[]
  studies: Study[]
  isLoadingStudies: boolean
  user: User

  constructor(
    private userService: CurrentUserService,
    private editorStudyService: EditorStudyService,
    private breadcrumbService: BreadcrumbService
  ) { }

  ngOnInit() {
    this.isLoadingStudies = true
    this.editorStudyService.getAll()
      .subscribe(studies => {
        this.studies = studies

        this.userService.getUserOrganizations()
          .subscribe(organizations => this.organizations = organizations)
        this.userService.getCurrentUserObservable()
          .subscribe(user => this.user = user)

        this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable()

        this.isLoadingStudies = false
    })
  }

}
