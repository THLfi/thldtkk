
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env } from '../../environments/environment'
import { Observable } from 'rxjs'


import { Dataset } from '../model2/dataset'
import { GrowlMessageService } from '../services-common/growl-message.service'
import { InstanceVariable } from '../model2/instance-variable'
import { NodeUtils } from '../utils/node-utils'
import { PopulationService } from '../services-common/population.service'
import { Study } from '../model2/study'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class EditorStudyService {

  constructor(
    private populationService: PopulationService,
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private http: HttpClient
    ) {}

  public initNew(): Study {
    return this.initializeProperties({
      id: null,
      prefLabel: null,
      altLabel: null,
      abbreviation: null,
      description: null,
      usageConditionAdditionalInformation: null,
      freeConcepts: null,
      registryPolicy: null,
      purposeOfPersonRegistry: null,
      usageOfPersonalInformation: null,
      personRegistrySources: null,
      personRegisterDataTransfers: null,
      personRegisterDataTransfersOutsideEuOrEea: null,
      profilingAndAutomation: null,
      profilingAndAutomationDescription: null,
      groundsForConfidentiality: null,
      principlesForPhysicalSecurity: [],
      principlesForDigitalSecurity: [],
      otherPrinciplesForPhysicalSecurity: [],
      otherPrinciplesForDigitalSecurity: [],
      legalBasisForHandlingPersonalData: [],
      otherLegalBasisForHandlingPersonalData: null,
      containsSensitivePersonalData: null,
      legalBasisForHandlingSensitivePersonalData: [],
      otherLegalBasisForHandlingSensitivePersonalData: null,
      typeOfSensitivePersonalData: [],
      otherTypeOfSensitivePersonalData: null,
      isScientificStudy: null,
      systemInRoles: [],
      existenceForms: [],
      personInRoles: [],
      associatedOrganizations: [],
      links: [],
      conceptsFromScheme: [],
      datasetTypes: [],
      datasets: [],
      predecessors: [],
      successors: []
    })
  }

  initializeProperties(study: Study): Study {
    this.initProperties(study, [
      'prefLabel',
      'altLabel',
      'abbreviation',
      'description',
      'usageConditionAdditionalInformation',
      'freeConcepts',
      'registryPolicy',
      'purposeOfPersonRegistry',
      'usageOfPersonalInformation',
      'personRegistrySources',
      'personRegisterDataTransfers',
      'personRegisterDataTransfersOutsideEuOrEea',
      'otherLegalBasisForHandlingPersonalData',
      'otherLegalBasisForHandlingSensitivePersonalData',
      'otherTypeOfSensitivePersonalData',
      'partiesAndSharingOfResponsibilityInCollaborativeStudy',
      'studyPerformers',
      'profilingAndAutomationDescription',
      'groundsForConfidentiality',
      'directIdentityInformationDescription'
    ])

    if (!study.population) {
      study.population = this.populationService.initNew()
    }

    return study
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.translateService.currentLang ])
  }

  search(searchString: string, maxResults: number): Observable<Study[]> {
      const url = env.contextPath
      + env.apiPath
      + '/editor/studies?query='
      + searchString
      + '&max='
      + maxResults
      return this.http.get<Study[]>(url);
  }

  getAll(): Observable<Study[]> {
    return this.http.get<Study[]>(env.contextPath + env.apiPath + '/editor/studies');
  }

  getStudy(id: string, includeDatasets: boolean = true): Observable<Study> {
    return this.http.get<Study>(env.contextPath + env.apiPath + '/editor/studies/' + id + '?includeDatasets=' + includeDatasets);
  }

  save(study: Study): Observable<Study> {
    return this.saveStudyInternal(study).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.study.save.result.success')
      }))
  }

  private saveStudyInternal(study: Study): Observable<Study> {
    return this.http.post<Study>(env.contextPath + env.apiPath + '/editor/studies', study);
  }

  delete(studyId: string): Observable<any> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId

    return this.http.delete(path).pipe(
      tap(() => this.growlMessageService.buildAndShowMessage('info', 'operations.study.delete.result.success')))
  }

  saveDataset(studyId: string, dataset: Dataset): Observable<Dataset> {

    return this.http.post<Dataset>(env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets', dataset).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.dataset.save.result.success')
      }))
  }

  saveInstanceVariable(studyId: string, datasetId: string, instanceVariable: InstanceVariable): Observable<InstanceVariable> {

    const url = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/intanceVariables'

    return this.http.post<InstanceVariable>(url, instanceVariable).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceVariable.save.result.success')
      }))
  }

  publish(study: Study): Observable<Study> {
    return this.changeStudyState(study, 'publish');
  }

  withdraw(study: Study): Observable<Study> {
    return this.changeStudyState(study, 'withdraw');
  }

  reissue(study: Study): Observable<Study> {
    return this.changeStudyState(study, 'reissue');
  }

  private changeStudyState(study: Study, urlPart: string): Observable<Study> {
    return this.http.post<Study>(env.contextPath + env.apiPath + '/editor/study-functions/' + urlPart + '?studyId=' + study.id, {});
  }
}
