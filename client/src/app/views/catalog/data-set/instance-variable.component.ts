import { Component, Input } from '@angular/core';

import { InstanceVariable } from '../../../model/instance-variable';

@Component({
  selector: 'instance-variable',
  template: `
  <property-value [props]="variable.properties" [key]="'prefLabel'"></property-value>
  <property-value [props]="variable.properties" [key]="'description'"></property-value>
  `
})
export class InstanceVariableComponent {

  @Input() variable: InstanceVariable;

}
