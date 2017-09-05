import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs"
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { InstanceVariable } from '../model2/instance-variable';
import { Variable } from '../model2/variable';

@Injectable()
export class VariableService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private _http: Http
  ) { }

  getAllVariables(): Observable<Variable[]> {
        return this._http.get(env.contextPath + '/api/v2/variables')
            .map(response => response.json() as Variable[]);
  }

  getVariable(id:string): Observable<Variable> {
    return this._http.get(env.contextPath + '/api/v2/variables/' + id)
      .map(response => response.json() as Variable)
  }

  saveVariable(variable: Variable): Observable<Variable> {
    const path: string = env.contextPath
      + '/api/v2/variables/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, variable, options)
      .map(response => response.json() as Variable)
      .do(variable => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.variable.save.result.success')
      })
  }

  searchVariable(searchText: string): Observable<Variable[]> {
    return this._http.get(env.contextPath + '/api/v2/variables?query=' + searchText + '&max=50')
      .map(response => response.json() as Variable[])
  }

  getInstanceVariables(id:string):Observable<InstanceVariable[]> {
      return this._http.get(env.contextPath + '/api/v2/variables/'+id+'/instanceVariables')
      .map(response => response.json() as InstanceVariable[])

  }

  deleteVariable(variableId: string): Observable<any> {
    const path: string = env.contextPath
      + '/api/v2/variables/'
      + variableId

    return this._http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.variable.delete.result.success')
      })
  }

}
