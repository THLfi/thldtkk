import { Injectable } from '@angular/core'
import { Headers, Http, RequestOptions } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from '../services2/growl-message.service'
import { InstanceVariable } from '../model2/instance-variable'
import { Variable } from '../model2/variable'

@Injectable()
export class VariableService3 {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  search(searchText: string): Observable<Variable[]> {
    return this.http.get(env.contextPath + '/api/v3/variables?query=' + searchText + '&max=50')
      .map(response => response.json() as Variable[])
  }

  get(variableId: string): Observable<Variable> {
    return this.http.get(env.contextPath + '/api/v3/variables/' + variableId)
      .map(response => response.json() as Variable)
  }

  getAll(): Observable<Variable[]> {
    return this.http.get(env.contextPath + '/api/v3/variables')
      .map(response => response.json() as Variable[]);
  }

  save(variable: Variable): Observable<Variable> {
    const path: string = env.contextPath + '/api/v3/variables'
    const headers = new Headers({'Content-Type': 'application/json;charset=UTF-8'})
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, variable, options)
      .map(response => response.json() as Variable)
      .do(variable => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.variable.save.result.success')
      })
  }

  delete(variableId: string): Observable<any> {
    const path: string = env.contextPath + '/api/v3/variables/' + variableId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.variable.delete.result.success')
      })
  }

  getInstanceVariables(variableId: string): Observable<InstanceVariable[]> {
    const url = env.contextPath
      + '/api/v3/public/variables/'
      + variableId
      +'/instanceVariables'
    return this.http.get(url).map(response => response.json() as InstanceVariable[])
  }

}
