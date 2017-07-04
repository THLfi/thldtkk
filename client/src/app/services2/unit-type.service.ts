import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { UnitType } from '../model2/unit-type'
import { NodeUtils } from '../utils/node-utils'

@Injectable()
export class UnitTypeService {

  constructor(
    private nodeUtils: NodeUtils,
    private _http: Http,
    private translateService: TranslateService
  ) { }

  getAllUnitTypes(): Observable<UnitType[]> {
    return this._http.get(env.contextPath + '/api/v2/unitTypes?query=')
          .map(response => response.json() as UnitType[])
  }

  save(unitType: UnitType): Observable<UnitType> {
    const path: string = env.contextPath + '/api/v2/unitTypes/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, unitType, options)
      .map(response => response.json() as UnitType)
  }

  initNew(): UnitType {
    const unitType = {
      prefLabel: null,
      description: null
    }

    this.nodeUtils.initLangValuesProperties(unitType,
      [ 'prefLabel', 'description' ],
      [ this.translateService.currentLang ])

    return unitType
  }

}
