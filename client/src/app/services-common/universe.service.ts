import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Dataset } from '../model2/dataset'
import { NodeUtils } from '../utils/node-utils'
import { Universe } from '../model2/universe'

@Injectable()
export class UniverseService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: Http,
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) { }

  search(searchText = ""): Observable<Universe[]> {
      return this.http.get(env.contextPath + env.apiPath + '/universes?query=' + searchText + '&max=50')
        .map(response => response.json() as Universe[])
  }

  delete(universeId: string): Observable<any> {
      const path: string = env.contextPath + env.apiPath + '/universes/' + universeId

      return this.http.delete(path)
        .map(response => response.json())
        .do(() => {
          this.growlMessageService.buildAndShowMessage('info', 'operations.common.delete.result.success')
        })
  }

  getAll(): Observable<Universe[]> {
    return this.http.get(env.contextPath + env.apiPath + '/universes?query=')
          .map(response => response.json() as Universe[])
  }

  save(universe: Universe): Observable<Universe> {
    const path: string = env.contextPath + env.apiPath + '/universes/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, universe, options)
      .map(response => response.json() as Universe)
  }

  initNew(): Universe {
    const universe = {
      id: null,
      prefLabel: null,
      description: null
    }

    this.nodeUtils.initLangValuesProperties(universe,
      [ 'prefLabel', 'description' ],
      [ this.translateService.currentLang ])

    return universe
  }

  getUniverseDatasets(universe: Universe): Observable<Dataset[]> {
        const path: string = env.contextPath
          + env.apiPath
          + '/universes/'
          + universe.id
          + '/datasets'

        return this.http.get(path)
          .map(response => response.json() as Dataset[])
  }
}
