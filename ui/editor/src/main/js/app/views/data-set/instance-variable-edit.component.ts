import { ActivatedRoute, Router } from "@angular/router";
import { Component, OnInit } from '@angular/core';

import { DataSetService } from "../../services/data-set.service";
import { InstanceVariable } from '../../model/instance-variable';
import { InstanceVariableService } from "../../services/instance-variable.service";
import { NodeUtils } from "../../utils/node-utils";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: './instance-variable-edit.component.html'
})
export class InstanceVariableEditComponent implements OnInit {

  instanceVariable: InstanceVariable;

  constructor(
    private dataSetService: DataSetService,
    private instanceVariableService: InstanceVariableService,
    private nodeUtils: NodeUtils,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService
  ) { }

  ngOnInit(): void {
    const instanceVariableId = this.route.snapshot.params['instanceVariableId'];
    if (instanceVariableId) {
      this.instanceVariableService.getInstanceVariable(instanceVariableId)
        .subscribe(instanceVariable => this.instanceVariable = this.initProperties(instanceVariable));
    }
    else {
      this.instanceVariable = this.initProperties(this.nodeUtils.createNode())
    }
  }

  private initProperties(instanceVariable: InstanceVariable): InstanceVariable {
    this.nodeUtils.initProperties(instanceVariable, [
      'prefLabel',
      'description',
      'referencePeriodStart',
      'referencePeriodEnd'
    ])

    instanceVariable.properties['referencePeriodStart'][0].lang = ''
    instanceVariable.properties['referencePeriodStart'][0].regex = "^\\d{4}-\\d{2}-\\d{2}$"
    instanceVariable.properties['referencePeriodEnd'][0].lang = ''
    instanceVariable.properties['referencePeriodEnd'][0].regex = "^\\d{4}-\\d{2}-\\d{2}$"

    return instanceVariable
  }

  save(): void {
    this.instanceVariableService.updateInstanceVariable(this.instanceVariable)
      .subscribe(savedInstanceVariable => {
        const datasetId = this.route.snapshot.params['dataSetId']
        this.dataSetService.getDataSet(datasetId)
          .subscribe(dataSet => {
            if (!dataSet.references['instanceVariable']) {
              dataSet.references['instanceVariable'] = []
            }
            dataSet.references['instanceVariable'].push(savedInstanceVariable)

            this.dataSetService.saveDataSet(dataSet)
              .subscribe(() => {
                this.instanceVariable = savedInstanceVariable
                this.goBack()
              })
          })
      });
  }

  confirmRemove(): void {
    this.translateService.get('confirmInstanceVariableDelete')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.instanceVariableService.remove(this.instanceVariable)
            .subscribe(response => this.goBack())
        }
      })
  }

  goBack(): void {
    this.router.navigate(['/datasets', this.route.snapshot.params['dataSetId']])
  }

}
