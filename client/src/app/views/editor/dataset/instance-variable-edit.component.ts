import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { DatasetService } from '../../../services2/dataset.service';
import { InstanceVariable } from '../../../model2/instance-variable';
import { InstanceVariableService } from '../../../services2/instance-variable.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  templateUrl: './instance-variable-edit.component.html'
})
export class InstanceVariableEditComponent implements OnInit {

  instanceVariable: InstanceVariable
  currentLang: string

  constructor(
    private datasetService: DatasetService,
    private instanceVariableService: InstanceVariableService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService
  ) {
    this.currentLang = translateService.currentLang
  }

  ngOnInit(): void {
    const datasetId = this.route.snapshot.params['datasetId']
    const instanceVariableId = this.route.snapshot.params['instanceVariableId']

    if (instanceVariableId) {
      this.instanceVariableService.getInstanceVariable(datasetId, instanceVariableId)
        .subscribe(instanceVariable => {
          this.instanceVariable = this.initInstanceVariable(instanceVariable)
        })
    }
    else {
      this.instanceVariable = this.initInstanceVariable({
        id: null,
        prefLabel: null,
        description: null,
        referencePeriodStart: null,
        referencePeriodEnd: null
      })
    }
  }

  private initInstanceVariable(instanceVariable: InstanceVariable): InstanceVariable {
    return this.initProperties(instanceVariable, ['prefLabel', 'description'])
  }

  private initProperties(instanceVariable: InstanceVariable, properties: string[]): InstanceVariable {
    properties.forEach(property => {
      if (!instanceVariable[property]) {
        instanceVariable[property] = {}
      }
      if (!instanceVariable[property][this.currentLang]) {
        instanceVariable[property][this.currentLang] = ''
      }
    })
    return instanceVariable
  }

  save(): void {
    const datasetId = this.route.snapshot.params['datasetId']
    this.instanceVariableService.saveInstanceVariable(datasetId, this.instanceVariable)
      .subscribe(instanceVariable => {
        this.instanceVariable = instanceVariable
        this.goBack()
      })
  }

  confirmRemove(): void {
    this.translateService.get('confirmInstanceVariableDelete')
      .subscribe((message: string) => {
        if (confirm(message)) {
          
          const datasetId = this.route.snapshot.params['datasetId'];
          const instanceVariableId = this.route.snapshot.params['instanceVariableId'];

          this.instanceVariableService.deleteInstanceVariable(datasetId, instanceVariableId).subscribe(
            result => this.goBack()
          );
        }
      })
  }

  goBack(): void {
    this.router.navigate(['/editor/datasets', this.route.snapshot.params['datasetId']])
  }

}
