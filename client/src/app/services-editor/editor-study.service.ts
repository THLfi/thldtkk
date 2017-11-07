import { environment as env } from '../../environments/environment'
import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { NodeUtils } from '../utils/node-utils'
import { Observable } from 'rxjs'
import { PopulationService } from '../services-common/population.service'
import { Study } from '../model2/study'
import { TranslateService } from '@ngx-translate/core'

import 'rxjs/add/operator/map'

@Injectable()
export class EditorStudyService {

  constructor(
    private populationService: PopulationService,
    private translateService: TranslateService,
    private nodeUtils: NodeUtils,
    private http: Http) {}

  public initNew(): Study {
    return this.initializeProperties({
      id: null,
      prefLabel: null,
      altLabel: null,
      abbreviation: null,
      description: null,
      registryPolicy: null,
      usageConditionAdditionalInformation: null,
      published: null,
      referencePeriodStart: null,
      referencePeriodEnd: null,
      collectionStartDate: null,
      collectionEndDate: null,
      ownerOrganization: null,
      ownerOrganizationUnit: [],
      usageCondition: null,
      lifecyclePhase: null,
      population: null,
      numberOfObservationUnits: null,
      comment: null,
      conceptsFromScheme: [],
      links: [],
      freeConcepts: null,
      datasetTypes: [],
      unitType: null,
      universe: null,
      personInRoles: [],
      datasets: [],
      predecessors: [],
      successors: []
    })
  }

  initializeProperties(study: Study): Study {
    this.initProperties(study, [
      'prefLabel',
      'abbreviation',
      'altLabel',
      'description',
      'registryPolicy',
      'usageConditionAdditionalInformation',
      'freeConcepts'
    ])

    if (!study.population) {
      study.population = this.populationService.initNew()
    }

    return study
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.translateService.currentLang ])
  }

  getAll(): Observable<Study[]> {
    return Observable.throw("Not implemented")
  }

  getStudy(id: string): Observable<Study> {
    return Observable.throw("Not implemented")  
  }

  save(study: Study): Observable<Study> {
    return Observable.throw("Not implemented")
  }

  publish(study: Study): Observable<Study> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return Observable.throw("Not implemented")
  }

  withdraw(study: Study): Observable<Study> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return Observable.throw("Not implemented")
  }

}