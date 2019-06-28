
import {of as observableOf,  Observable, Subject } from 'rxjs';

import {catchError, switchMap, distinctUntilChanged, debounceTime} from 'rxjs/operators';
import { Component, OnInit } from '@angular/core'

import { Study } from '../../../model2/study'
import { Organization } from '../../../model2/organization'
import { OrganizationService } from '../../../services-common/organization.service'
import { PublicStudyService } from '../../../services-public/public-study.service'








@Component({
  templateUrl: './catalog-study-list.component.html',
  styleUrls: [ './catalog-study-list.component.css' ]
})
export class CatalogStudyListComponent implements OnInit {

  organizations: Organization[]
  studies: Study[]

  searchTerms: Subject<string>
  searchText = ''
  selectedOrganizationId = ''

  loadingStudies: boolean
  readonly searchDelay = 300;

  constructor(
    private organizationService: OrganizationService,
    private studyService: PublicStudyService
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit(): void {
    this.organizationService.getAllOrganizations()
      .subscribe(organizations => {
        this.organizations = organizations
      })
    this.initSearchSubscription(this.searchTerms)
    this.instantSearchStudies(this.searchText)
  }

  selectOrganization(organizationId: string) {
    this.selectedOrganizationId = organizationId
    this.instantSearchStudies(this.searchText)
  }

  searchStudies(literalSearchTerms: string): void {
    this.searchTerms.next(literalSearchTerms)
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.pipe(debounceTime(this.searchDelay),
      distinctUntilChanged(),
      switchMap(term => {
        this.loadingStudies = true
        return this.studyService.search(term, this.selectedOrganizationId)
      }),
      catchError(() => {
        this.initSearchSubscription(searchTerms)
        return observableOf<Study[]>([])
      }),)
      .subscribe(studies => {
        this.studies = studies
        this.loadingStudies = false
      })
  }

  instantSearchStudies(literalSearchTerms: string): void {
    this.loadingStudies = true
    this.studyService.search(literalSearchTerms, this.selectedOrganizationId)
      .subscribe(studies => {
      this.studies = studies
      this.loadingStudies = false
    })
  }

}
