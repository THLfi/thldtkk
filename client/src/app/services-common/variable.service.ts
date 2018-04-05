import { Injectable } from '@angular/core'
import { Headers, Http, RequestOptions } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { InstanceVariable } from '../model2/instance-variable'
import { Variable } from '../model2/variable'
import { Dataset } from '../model2/dataset'

@Injectable()
export class VariableService {

  constructor(
    private translateService : TranslateService,
    private nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  search(searchText = ""): Observable<Variable[]> {
    return this.http.get(env.contextPath + env.apiPath + '/variables?query=' + searchText + '&max=50')
      .map(response => response.json() as Variable[])
  }

  get(variableId: string): Observable<Variable> {
    return this.http.get(env.contextPath + env.apiPath + '/variables/' + variableId)
      .map(response => response.json() as Variable)
  }

  getAll(): Observable<Variable[]> {
    return this.http.get(env.contextPath + env.apiPath + '/variables')
      .map(response => response.json() as Variable[]);
  }

  save(variable: Variable): Observable<Variable> {
    const path: string = env.contextPath + env.apiPath + '/variables'
    const headers = new Headers({'Content-Type': 'application/json;charset=UTF-8'})
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, variable, options)
      .map(response => response.json() as Variable)
      .do(variable => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.variable.save.result.success')
      })
  }

  delete(variableId: string): Observable<any> {
    const path: string = env.contextPath + env.apiPath + '/variables/' + variableId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.variable.delete.result.success')
      })
  }

  getInstanceVariables(variableId: string, graph: string): Observable<InstanceVariable[]> {
    const url = env.contextPath
      + env.apiPath
      + '/' + graph + '/variables/'
      + variableId
      +'/instanceVariables'
    return this.http.get(url).map(response => response.json() as InstanceVariable[])
  }

  initNew(): Variable {
    const variable = {
      id: null,
      prefLabel: null,
      description: null
    }
    this.nodeUtils.initLangValuesProperties(variable,
          [ 'prefLabel', 'description' ],
          [ this.translateService.currentLang ])

    return variable
  }

  getVariableDatasets(variable: Variable): Observable<Dataset[]> {
      const path: string = env.contextPath
        + env.apiPath + '/variables/'
        + variable.id
        + '/datasets'

      return this.http.get(path)
        .map(response => response.json() as Dataset[])
    }

    getVariableInstanceVariables(variable: Variable): Observable<InstanceVariable[]> {
        const path: string = env.contextPath
          + env.apiPath
          + '/editor/variables/'
          + variable.id
          + '/instanceVariables'

        return this.http.get(path)
          .map(response => response.json() as InstanceVariable[])
      }
}
