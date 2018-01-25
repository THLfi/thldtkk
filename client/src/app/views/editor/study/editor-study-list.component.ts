import { Component, OnInit } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'
import { Observable, Subject } from 'rxjs'
import { ActivatedRoute, Router, UrlTree } from '@angular/router'
import { Location } from '@angular/common'

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
  studies: Study[] = []
  user: User

  searchText: string = ""
  maxResults: number = 50

  static readonly searchDelay = 500;
  static defaultMaxResults = 50

  searchTerms: Subject<string>
  latestLookupTerm: string

  isLoadingStudies: boolean = false
  deleteInProgress: boolean = false

  constructor(
    private breadcrumbService: BreadcrumbService,
    private currentUserService: CurrentUserService,
    private editorStudyService: EditorStudyService,
    private translateService: TranslateService,
    private route: ActivatedRoute,
    private location: Location,
    private router: Router
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit() {
    this.loadUserAndOrganizations()
    this.route.queryParams.subscribe(params => {
      this.searchText = String(params['query'] || '')

      this.maxResults = Number(params['max'] || EditorStudyListComponent.defaultMaxResults);
      if(this.searchText != null) {
        this.isLoadingStudies = true
        this.searchStudies(this.searchText)
        .subscribe(studies => {
           this.updateQueryParam(this.searchText)
           this.studies = studies;
           this.isLoadingStudies = false
         });
        this.updateQueryParam(this.searchText)
      }
    })
    this.initSearchSubscription(this.searchTerms)
  }

  searchStudies(searchText: string = this.searchText): Observable<Study[]> {
    return this.editorStudyService.search(searchText, this.maxResults)
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

  delayedSearchStudies(searchText:string): void {
    this.searchTerms.next(searchText)
  }

  loadMoreResults(): void {
    this.maxResults += 100
    this.isLoadingStudies = true

    this.searchStudies(this.searchText)
      .subscribe(studies => {
        this.updateQueryParam(this.searchText)
        this.studies = studies;
        this.isLoadingStudies = false
      });
  }

  private updateQueryParam(searchText:string): void {
    let urlTree:UrlTree = this.router.parseUrl(this.router.url)
    urlTree.queryParams['query'] = this.searchText
    urlTree.queryParams['max'] = String(this.maxResults)

    let updatedUrl = this.router.serializeUrl(urlTree);
    this.location.replaceState(updatedUrl);
  }

  private initSearchSubscription(searchTerms:Subject<string> ): void {
    searchTerms.debounceTime(EditorStudyListComponent.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.isLoadingStudies = true;
        this.latestLookupTerm = term;
        this.maxResults = EditorStudyListComponent.defaultMaxResults
        return term || term === '' ? this.searchStudies(term) : Observable.of<Study[]>([])
      })
      .catch(error => {
        this.initSearchSubscription(searchTerms)
        return Observable.of<Study[]>([])
      })
      .subscribe(studies => {
        this.updateQueryParam(this.searchText)
        this.studies = studies
        this.isLoadingStudies = false})
  }

}
