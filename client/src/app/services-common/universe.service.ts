
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Dataset } from '../model2/dataset'
import { NodeUtils } from '../utils/node-utils'
import { Study } from '../model2/study'
import { Universe } from '../model2/universe'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UniverseService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: HttpClient,
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) { }

  search(searchText = ""): Observable<Universe[]> {
      return this.http.get<Universe[]>(env.contextPath + env.apiPath + '/universes?query=' + searchText + '&max=50');
  }

  delete(universeId: string): Observable<any> {
      const path: string = env.contextPath + env.apiPath + '/universes/' + universeId

      return this.http.delete(path).pipe(
        tap(() => {
          this.growlMessageService.buildAndShowMessage('info', 'operations.common.delete.result.success')
        }))
  }

  getAll(): Observable<Universe[]> {
    return this.http.get<Universe[]>(env.contextPath + env.apiPath + '/universes?query=');
  }

  save(universe: Universe): Observable<Universe> {
    const path: string = env.contextPath + env.apiPath + '/universes/'

    return this.http.post<Universe>(path, universe);
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

        return this.http.get<Dataset[]>(path);
  }

  getUniverseStudies(universe: Universe): Observable<Study[]> {
        const path: string = env.contextPath
          + env.apiPath
          + '/universes/'
          + universe.id
          + '/studies'

        return this.http.get<Study[]>(path);
  }
}
