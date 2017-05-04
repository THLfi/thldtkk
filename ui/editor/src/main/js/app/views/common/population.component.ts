import { Component, Input } from '@angular/core';

import { Population } from '../../model/population';

@Component({
  selector: 'population',
  templateUrl: './population.component.html'
})
export class PopulationComponent {

  @Input() population: Population;

}
