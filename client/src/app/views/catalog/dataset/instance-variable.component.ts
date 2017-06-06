import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { Dataset } from '../../../model2/dataset'
import { DatasetService } from '../../../services2/dataset.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableService } from '../../../services2/instance-variable.service'

@Component({
  templateUrl: './instance-variable.component.html'
})
export class InstanceVariableComponent implements OnInit {

  instanceVariable: InstanceVariable
  dataset: Dataset
  instanceVariableId: string
  datasetId: string
  language: string

  constructor(private instanceVariableService: InstanceVariableService,
              private datasetService: DatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService) {
    this.datasetId = this.route.snapshot.params['datasetId']
    this.instanceVariableId = this.route.snapshot.params['instanceVariableId']
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getInstanceVariable()
  }

  private getInstanceVariable() {
    Observable.forkJoin(
      this.instanceVariableService.getInstanceVariable(this.datasetId, this.instanceVariableId),
      this.datasetService.getDataset(this.datasetId)
    ).subscribe(data => {
      this.instanceVariable = data[0]
      this.dataset = data[1]
    })
  }

}
