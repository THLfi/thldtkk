import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { UnitType } from '../model2/unit-type'
import { Dataset } from '../model2/dataset'
import { InstanceVariable } from '../model2/instance-variable'

@Injectable()
export class UnitTypeService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: Http,
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService
  ) { }

  search(searchTerms = ""): Observable<UnitType[]> {
    return this.http.get(env.contextPath + '/api/v3/unitTypes?query=' + searchTerms)
      .map(response => response.json() as UnitType[])
  }

  getAll(): Observable<UnitType[]> {
    return this.http.get(env.contextPath + '/api/v3/unitTypes?query=')
      .map(response => response.json() as UnitType[])
  }

  save(unitType: UnitType): Observable<UnitType> {
    const path: string = env.contextPath + '/api/v3/unitTypes'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, unitType, options)
      .map(response => response.json() as UnitType)
  }

  delete(unitType:UnitType) {
    const path: string = env.contextPath + '/api/v3/unitTypes/' + unitType.id

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.unitType.delete.result.success')
      })
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
      + '/api/v3/unitTypes/'
      + unitType.id
      + '/datasets'

    return this.http.get(path)
      .map(response => response.json() as Dataset[])
  }

  getUnitTypeInstanceVariables(unitType: UnitType): Observable<InstanceVariable[]> {
    const path: string = env.contextPath
      + '/api/v3/unitTypes/'
      + unitType.id
      + '/instanceVariables'

    return this.http.get(path)
      .map(response => response.json() as InstanceVariable[])
  }

}
