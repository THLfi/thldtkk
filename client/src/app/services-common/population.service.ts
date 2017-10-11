import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { NodeUtils } from '../utils/node-utils'
import { Population } from '../model2/population'

@Injectable()
export class PopulationService {

  constructor(
    private nodeUtils: NodeUtils,
    private translateService: TranslateService
  ) { }

  initNew(): Population {
    const population = {
      id: null,
      prefLabel: null,
      geographicalCoverage: null,
      sampleSize: null,
      loss: null
    }

    this.nodeUtils.initLangValuesProperties(population,
      [
        'prefLabel',
        'geographicalCoverage',
        'sampleSize',
        'loss'
      ],
      [ this.translateService.currentLang ])

    return population
  }

}
