import { Component, OnInit } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'
import { Observable, Subject } from 'rxjs'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { Organization } from '../../../model2/organization'
import { Study } from '../../../model2/study'
import { User } from '../../../model2/user'

@Component({
  templateUrl: './editor-study-list.component.html'
})
export class EditorStudyListComponent implements OnInit {

  organizations: Organization[]
  studies: Study[]
  user: User

  searchTerms: string = ""
  maxResults: number = 50

  static readonly searchDelay = 500;

  searchItem: Subject<string> = new Subject<string>()

  isLoadingStudies: boolean = false
  deleteInProgress: boolean = false

  constructor(
    private breadcrumbService: BreadcrumbService,
    private currentUserService: CurrentUserService,
    private editorStudyService: EditorStudyService,
    private translateService: TranslateService
  ) { }

  ngOnInit() {
    this.searchStudies()
    this.loadUserAndOrganizations()
  }

  loadMoreResults(): void {
    this.maxResults += 50
    this.searchStudies(this.searchTerms)
  }

  searchStudies(searchString: string = this.searchTerms) {
    this.isLoadingStudies = true
    this.searchTerms = searchString
    this.editorStudyService.searchStudies(searchString, this.maxResults)
      .subscribe(studies => {
        this.studies = studies
        this.isLoadingStudies = false
      })
  }

  private loadUserAndOrganizations() {
    this.currentUserService.getCurrentUserObservable()
      .subscribe(user => this.user = user)
    this.currentUserService.getUserOrganizations()
      .subscribe(organizations => this.organizations = organizations)
  }

  confirmRemove(event: any, studyId: string): void {
    event.stopPropagation()

    this.translateService.get('study.confirmRemove')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.deleteInProgress = true

          this.editorStudyService.delete(studyId)
            .finally(() => this.deleteInProgress = false)
            .subscribe(() => this.searchStudies())
        }
      })
  }

}
