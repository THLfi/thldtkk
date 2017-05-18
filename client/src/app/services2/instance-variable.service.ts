import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs"
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { InstanceVariable } from '../model2/instance-variable';

@Injectable()
export class InstanceVariableService {

  constructor(
    private _http: Http
  ) { }

  getInstanceVariable(datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + '/api/v2/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this._http.get(path)
      .map(response => response.json() as InstanceVariable)
  }

  saveInstanceVariable(datasetId: string, instanceVariable: InstanceVariable): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + '/api/v2/datasets/'
      + datasetId
      + '/instanceVariables'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, instanceVariable, options)
      .map(response => response.json() as InstanceVariable)
  }

}
