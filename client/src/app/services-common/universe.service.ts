import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { NodeUtils } from '../utils/node-utils'
import { Universe } from '../model2/universe'

@Injectable()
export class UniverseService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: Http,
    private translateService: TranslateService
  ) { }

  getAll(): Observable<Universe[]> {
    return this.http.get(env.contextPath + '/api/v3/universes?query=')
          .map(response => response.json() as Universe[])
  }

  save(universe: Universe): Observable<Universe> {
    const path: string = env.contextPath + '/api/v3/universes/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, universe, options)
      .map(response => response.json() as Universe)
  }

  initNew(): Universe {
    const universe = {
      prefLabel: null,
      description: null
    }

    this.nodeUtils.initLangValuesProperties(universe,
      [ 'prefLabel', 'description' ],
      [ this.translateService.currentLang ])

    return universe
  }

}
