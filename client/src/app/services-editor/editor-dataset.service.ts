
import {throwError as observableThrowError,  Observable } from 'rxjs';

import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'


import { environment as env} from '../../environments/environment'

import { Dataset } from '../model2/dataset'
import { GrowlMessageService } from '../services-common/growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { PopulationService } from '../services-common/population.service'
import { TranslateService } from '@ngx-translate/core'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class EditorDatasetService {

  constructor(
    private populationService: PopulationService,
    private nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private http: HttpClient,
    private translateService: TranslateService
  ) { }

  search(searchText: string): Observable<Dataset[]> {
    return this.http.get<Dataset[]>(env.contextPath + env.apiPath + '/editor/datasets?query=' + searchText + '&max=50');
  }

  getAll(): Observable<Dataset[]> {
    return this.http.get<Dataset[]>(env.contextPath + env.apiPath + '/editor/datasets');
  }

  getDataset(studyId: string, datasetId: string): Observable<Dataset> {
    return this.http.get<Dataset>(env.contextPath + env.apiPath + '/editor/studies/' + studyId + '/datasets/' + datasetId);
  }

  save(dataset: Dataset): Observable<Dataset> {
    return this.saveDatasetInternal(dataset).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.dataset.save.result.success')
      }))
  }

  private saveDatasetInternal(dataset: Dataset): Observable<Dataset> {
    return this.http.post<Dataset>(env.contextPath + env.apiPath + '/editor/datasets', dataset);
  }

  delete(studyId: string, datasetId: string): Observable<any> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId

    return this.http.delete(path).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.dataset.delete.result.success')
      }))
  }

  importDataset(event): Observable<Dataset> {
    return observableThrowError('Not implemented')
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
      ownerOrganizationUnit: null,
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
