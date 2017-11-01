import { Component, OnInit } from '@angular/core'

import { Dataset } from '../../../model2/dataset'
import { Organization } from '../../../model2/organization'
import { User } from '../../../model2/user'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { CurrentUserService } from '../../../services-editor/user.service'

@Component({
    templateUrl: './data-set-list.component.html',
    styleUrls: ['./data-set-list.component.css']
})
export class DatasetListComponent implements OnInit {

  datasets: Dataset[] = []
  isLoadingDatasets: boolean = false
  organizations: Organization[]
  user: User

  constructor(
    private dataSetService: EditorDatasetService,
    private userService: CurrentUserService
  ) { }

  ngOnInit(): void {
    this.isLoadingDatasets = true;
    this.dataSetService.getAll()
      .subscribe(dataSets => {
        this.datasets = dataSets
        this.userService.getUserOrganizations()
              .subscribe(organizations => this.organizations = organizations)
        this.userService.getCurrentUserObservable()
                      .subscribe(user => this.user = user)
        this.isLoadingDatasets = false
      })
  }

}
