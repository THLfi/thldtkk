import { Component, OnInit } from '@angular/core'
import { BehaviorSubject, Observable, Subscription } from 'rxjs'

import { CurrentUserService } from '../../../services-editor/user.service'
import { Organization } from '../../../model2/organization'
import { OrganizationService } from '../../../services-common/organization.service'
import { OrganizationUnit } from '../../../model2/organization-unit'
import { OrganizationUnitService } from '../../../services-common/organization-unit.service'
import { TranslateService } from '@ngx-translate/core'
import { ConfirmationService } from 'primeng/primeng'
import { LangPipe } from '../../../utils/lang.pipe'
import { StringUtils } from '../../../utils/string-utils'
import { environment } from 'environments/environment';

@Component({
  templateUrl: './organization-list.component.html',
  styleUrls: ['./organization-list.component.css']
})
export class OrganizationListComponent implements OnInit {

  organizations: Organization[] = []
  filteredOrganizations: Organization[] = []
  filteredOrganizationUnits: { [organizationId: string]: OrganizationUnit[] } = {}

  loadingOrganizations: boolean

  searchTermSubject: BehaviorSubject<string> = new BehaviorSubject<string>('')
  filterOrganizationWhenSearchTermChangesSubscription: Subscription

  editOrganization: Organization

  parentOrganizationForEditOrganizationUnit: Organization
  editOrganizationUnit: OrganizationUnit

  isUserAdmin: boolean = false

  constructor(
    private confirmationService: ConfirmationService,
    private currentUserService: CurrentUserService,
    private langPipe: LangPipe,
    private organizationService: OrganizationService,
    private organizationUnitService: OrganizationUnitService,
    private translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.refreshOrganizations()
  }

  refreshOrganizations() {
    this.loadingOrganizations = true

    this.organizations = []
    this.filteredOrganizations = []
    this.filteredOrganizationUnits = {}
    if (this.filterOrganizationWhenSearchTermChangesSubscription) {
      this.filterOrganizationWhenSearchTermChangesSubscription.unsubscribe()
    }

    this.currentUserService.getCurrentUserObservable().subscribe(user => {
      let organizationsObservable: Observable<Organization[]>

      this.isUserAdmin = user && user.isAdmin

      if (this.isUserAdmin) {
        organizationsObservable = this.organizationService.getAllOrganizations()
      }
      else {
        organizationsObservable = this.currentUserService.getUserOrganizations()
      }

      organizationsObservable
        .subscribe(organizations => {
          this.organizations = organizations
          this.filterOrganizationWhenSearchTermChangesSubscription = this.searchTermSubject.subscribe(searchTerm => {
            let filteredOrganizations = this.organizations.filter(organization => {
              if (StringUtils.isBlank(searchTerm)) {
                return true
              }
              else if (this.langPipe.transform(organization.prefLabel).toLowerCase().indexOf(searchTerm) > -1
                || this.langPipe.transform(organization.abbreviation).toLowerCase().indexOf(searchTerm) > -1) {
                return true
              }
              else if (organization.organizationUnit.filter(organizationUnit =>
                this.langPipe.transform(organizationUnit.prefLabel).toLowerCase().indexOf(searchTerm) > -1
                  || this.langPipe.transform(organizationUnit.abbreviation).toLowerCase().indexOf(searchTerm) > -1).length > 0) {
                return true
              }
              else {
                return false
              }
            })

            filteredOrganizations.forEach(organization => {
              this.filteredOrganizationUnits[organization.id] = organization.organizationUnit.filter(organizationUnit => {
                return StringUtils.isBlank(searchTerm)
                  || this.langPipe.transform(organizationUnit.prefLabel).toLowerCase().indexOf(searchTerm) > -1
                  || this.langPipe.transform(organizationUnit.abbreviation).toLowerCase().indexOf(searchTerm) > -1
              })
            })

            this.filteredOrganizations = filteredOrganizations
          })
          this.loadingOrganizations = false
        })
    })
  }

  updateSearchTerms(newSearchTerm: string) {
    this.searchTermSubject.next(newSearchTerm.toLowerCase())
  }

  showEditOrganizationModal(organization: Organization): void {
    if (organization == null) {
      this.editOrganization = this.organizationService.initNew()
    }
    else {
      this.editOrganization = organization
    }
  }

  saveOrganization(organization: Organization) {
    this.organizationService.save(organization).subscribe(() => {
      this.refreshOrganizations()
      this.closeOrganizationModal()
    })
  }

  closeOrganizationModal() {
    this.editOrganization = null
  }

  showEditOrganizationUnitModal(organization: Organization, organizationUnit: OrganizationUnit) {
    this.parentOrganizationForEditOrganizationUnit = organization
    if (organizationUnit) {
      this.editOrganizationUnit = organizationUnit
    }
    else {
      this.editOrganizationUnit = this.organizationUnitService.initNew()
    }
  }

  saveOrganizationUnit(organizationUnit) {
    this.organizationUnitService.save(organizationUnit)
      .subscribe(() => {
        this.refreshOrganizations()
        this.closeOrganizationUnitModal()
      })
  }

  closeOrganizationUnitModal() {
    this.editOrganizationUnit = null
  }

  confirmRemoveOrganizationUnit(organizationUnit: OrganizationUnit): void {
    const prefLabel = this.langPipe.transform(organizationUnit.prefLabel)
    const abbreviation = this.langPipe.transform(organizationUnit.abbreviation)

    let label = prefLabel
    if (StringUtils.isNotBlank(abbreviation)) {
      label += (' (' + abbreviation + ')')
    }

    Observable.forkJoin(
      this.organizationUnitService.getOrganizationUnitStudies(organizationUnit),
      this.organizationUnitService.getOrganizationUnitDatasets(organizationUnit)
    ).map(data => {
      return {
        organizationUnit: label,
        studyCount: data[0].length,
        publishedStudyCount: data[0].filter(study => study.published).length,
        datasetCount: data[1].length,
        publishedDatasetCount: data[1].filter(dataset => dataset.published).length
      }
    }).subscribe(translationParams => {
      this.translateService.get('confirmRemoveOrganizationUnitModal.message', translationParams)
        .subscribe((confirmationMessage: string) => {
          this.confirmationService.confirm({
            message: confirmationMessage,
            accept: () => {
              this.organizationUnitService.delete(organizationUnit.id)
                .subscribe(() => this.refreshOrganizations())
            }
          })
        })
    })
  }

  downloadProcessingActivities(organization: Organization) {
    const downloadProcessingActivitiesUrl = `${environment.contextPath}${environment.apiPath}/editor/organizations/${organization.id}/processing-activities`
    window.location.href = downloadProcessingActivitiesUrl;
  }
}
