
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { UnitType } from '../model2/unit-type'
import { Dataset } from '../model2/dataset'
import { InstanceVariable } from '../model2/instance-variable'
import { Study } from '../model2/study'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UnitTypeService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: HttpClient,
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService
  ) { }

  search(searchTerms = ""): Observable<UnitType[]> {
    return this.http.get<UnitType[]>(env.contextPath + env.apiPath + '/unitTypes?query=' + searchTerms);
  }

  getAll(): Observable<UnitType[]> {
    return this.http.get<UnitType[]>(env.contextPath + env.apiPath + '/unitTypes?query=');
  }

  save(unitType: UnitType): Observable<UnitType> {
    const path: string = env.contextPath + env.apiPath + '/unitTypes'

    return this.http.post<UnitType>(path, unitType);
  }

  delete(unitType:UnitType) {
    const path: string = env.contextPath + env.apiPath + '/unitTypes/' + unitType.id

    return this.http.delete(path).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.unitType.delete.result.success')
      }))
  }

  initNew(): UnitType {
    const unitType = {
      id: null,
      prefLabel: null,
      description: null
    }

    this.nodeUtils.initLangValuesProperties(unitType,
      [ 'prefLabel', 'description' ],
      [ this.translateService.currentLang ])

    return unitType
  }

  getUnitTypeDatasets(unitType: UnitType): Observable<Dataset[]> {
    const path: string = env.contextPath
      + env.apiPath
      + '/unitTypes/'
      + unitType.id
      + '/datasets'

    return this.http.get<Dataset[]>(path);
  }

  getUnitTypeInstanceVariables(unitType: UnitType): Observable<InstanceVariable[]> {
    const path: string = env.contextPath
      + env.apiPath
      + '/unitTypes/'
      + unitType.id
      + '/instanceVariables'

    return this.http.get<InstanceVariable[]>(path);
  }

  getUnitTypeStudies(unitType: UnitType): Observable<Study[]> {
    const path: string = env.contextPath
      + env.apiPath
      + '/unitTypes/'
      + unitType.id
      + '/studies'

    return this.http.get<Study[]>(path);
  }
}
