import { ActivatedRoute, Router } from "@angular/router";
import { Component, OnInit } from '@angular/core';

import { InstanceVariable } from '../../model/instance-variable';
import { InstanceVariableService } from "../../services/instance-variable.service";

@Component({
  templateUrl: './instance-variable-edit.component.html'
})
export class InstanceVariableEditComponent implements OnInit {

  instanceVariable: InstanceVariable;

  constructor(
    private instanceVariableService: InstanceVariableService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const instanceVariableId = this.route.snapshot.params['instanceVariableId'];
    this.instanceVariableService.getInstanceVariable(instanceVariableId)
      .subscribe(instanceVariable => this.instanceVariable = this.initMissingFields(instanceVariable));
  }

  private initMissingFields(instanceVariable: InstanceVariable): InstanceVariable {
    if (!instanceVariable.properties['prefLabel']) {
      instanceVariable.properties['prefLabel'] = [
        {
          lang: 'fi',
          value: ''
        }
      ]
    }
    if (!instanceVariable.properties['description']) {
      instanceVariable.properties['description'] = [
        {
          lang: 'fi',
          value: ''
        }
      ]
    }
    return instanceVariable;
  }

  save(): void {
    this.instanceVariableService.updateInstanceVariable(this.instanceVariable)
      .subscribe(instanceVariable => {
        this.instanceVariable = instanceVariable;
        this.goBack();
      });
  }

  goBack(): void {
    this.router.navigate(['/datasets', this.route.snapshot.params['dataSetId']]);
  }

}
