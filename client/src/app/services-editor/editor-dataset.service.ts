import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { Dataset } from '../model2/dataset'
import { GrowlMessageService } from '../services-common/growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { PopulationService } from '../services-common/population.service'
import { TranslateService } from '@ngx-translate/core'

@Injectable()
export class EditorDatasetService {

  constructor(
    private populationService: PopulationService,
    private nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private http: Http,
    private translateService: TranslateService
  ) { }

  search(searchText: string): Observable<Dataset[]> {
    return this.http.get(env.contextPath + '/api/v3/editor/datasets?query=' + searchText + '&max=50')
      .map(response => response.json() as Dataset[])
  }

  getAll(): Observable<Dataset[]> {
    return this.http.get(env.contextPath + '/api/v3/editor/datasets')
      .map(response => response.json() as Dataset[])
  }

  getDataset(studyId: string, datasetId: string): Observable<Dataset> {
    return this.http.get(env.contextPath + '/api/v3/editor/studies/' + studyId + '/datasets/' + datasetId)
      .map(response => response.json() as Dataset)
  }

  save(dataset: Dataset): Observable<Dataset> {
    return this.saveDatasetInternal(dataset)
      .do(dataset => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.dataset.save.result.success')
      })
  }

  private saveDatasetInternal(dataset: Dataset): Observable<Dataset> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(env.contextPath + '/api/v3/editor/datasets', dataset, options)
      .map(response => response.json() as Dataset)
  }

  delete(studyId: string, datasetId: string): Observable<any> {
    const path: string = env.contextPath
      + '/api/v3/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.dataset.delete.result.success')
      })
  }


  importDataset(event): Observable<Dataset> {
    return Observable.throw('Not implemented')
  }

  initNew(): Dataset {
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
      ownerOrganizationUnit: [],
      usageCondition: null,
      lifecyclePhase: null,
      population: null,
      instanceVariables: [],
      numberOfObservationUnits: null,
      comment: null,
      conceptsFromScheme: [],
      links: [],
      freeConcepts: null,
      datasetTypes: [],
      unitType: null,
      universe: null,
      personInRoles: [],
      predecessors: [],
      successors: []
    })
  }

  initializeProperties(dataset: Dataset): Dataset {
    this.initProperties(dataset, [
      'prefLabel',
      'abbreviation',
      'altLabel',
      'description',
      'registryPolicy',
      'usageConditionAdditionalInformation',
      'freeConcepts'
    ])

    if (!dataset.population) {
      dataset.population = this.populationService.initNew()
    }

    return dataset
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.translateService.currentLang ])
  }

}
