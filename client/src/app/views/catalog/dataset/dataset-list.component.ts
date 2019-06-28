import { Component, OnInit } from '@angular/core'


import { Dataset } from '../../../model2/dataset'
import { Organization } from '../../../model2/organization'
import { OrganizationService } from '../../../services-common/organization.service'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { Subscription } from 'rxjs'

@Component({
  templateUrl: './dataset-list.component.html'
})
export class DatasetListComponent implements OnInit {

  organizations: Organization[]
  datasets: Dataset[]

  searchText = ''
  selectedOrganizationId = ''

  onGoingSearchSubscription: Subscription

  constructor(
    private organizationService: OrganizationService,
    private datasetService: PublicDatasetService
  ) { }

  ngOnInit(): void {
    this.organizationService.getAllOrganizations()
      .subscribe(organizations => {
        this.organizations = organizations
      })
    this.doSearch()
  }

  selectOrganization(organizationId: string) {
    this.selectedOrganizationId = organizationId
    this.doSearch()
  }

  updateSearchText(query: string) {
    this.searchText = query
    this.doSearch()
  }

  private doSearch() {
    this.datasets = null

    if (this.onGoingSearchSubscription) {
      this.onGoingSearchSubscription.unsubscribe()
    }

    this.onGoingSearchSubscription = this.datasetService.search(
      this.searchText ? this.searchText : '',
      this.selectedOrganizationId ? this.selectedOrganizationId : ''
    ).subscribe(datasets => this.datasets = datasets)
  }

}
