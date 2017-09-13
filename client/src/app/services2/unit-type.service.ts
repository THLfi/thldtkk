import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { UnitType } from '../model2/unit-type'
import { InstanceVariable } from '../model2/instance-variable'
import { Dataset } from '../model2/dataset'
import { NodeUtils } from '../utils/node-utils'
import { GrowlMessageService } from './growl-message.service'

@Injectable()
export class UnitTypeService {

  constructor(
    private nodeUtils: NodeUtils,
    private _http: Http,
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService
  ) { }

  getAllUnitTypes(): Observable<UnitType[]> {
    return this.searchUnitTypes()
  }

  searchUnitTypes(searchTerms=""): Observable<UnitType[]> {
    return this._http.get(env.contextPath + '/api/v2/unitTypes/?query='+searchTerms)
          .map(response => response.json() as UnitType[])
  }

  getUnitTypeDatasets(unitType: UnitType): Observable<Dataset[]> {
    const path: string = env.contextPath
      + '/api/v2/unitTypes/'
      + unitType.id
      + '/datasets'

    return this._http.get(path)
          .map(response => response.json() as Dataset[])
  }

  getUnitTypeInstanceVariables(unitType: UnitType): Observable<InstanceVariable[]> {
    const path: string = env.contextPath
      + '/api/v2/unitTypes/'
      + unitType.id
      + '/instanceVariables'

    return this._http.get(path)
          .map(response => response.json() as InstanceVariable[])
  }

  save(unitType: UnitType): Observable<UnitType> {
    const path: string = env.contextPath + '/api/v2/unitTypes/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, unitType, options)
      .map(response => response.json() as UnitType)
  }

  delete(unitType:UnitType) {
    const path: string = env.contextPath
      + '/api/v2/unitTypes/'
      + unitType.id

    return this._http.delete(path)
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

}
