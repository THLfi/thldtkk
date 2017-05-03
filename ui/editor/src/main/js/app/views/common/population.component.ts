import { Component, Input } from '@angular/core';

import { Population } from '../../model/population';

@Component({
  selector: 'population',
  template: `<p><property-value [props]="population.properties" [key]="'prefLabel'"></property-value></p>`
})
export class PopulationComponent {

  @Input() population: Population;

}
