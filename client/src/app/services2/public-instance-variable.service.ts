import { Injectable } from '@angular/core'
import { Http } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/do'
import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { InstanceVariable } from '../model2/instance-variable'

@Injectable()
export class PublicInstanceVariableService {

  constructor(
    private _http: Http
  ) { }

  getInstanceVariable(datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + '/api/v2/public/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this._http.get(path)
      .map(response => response.json() as InstanceVariable)
  }

  searchInstanceVariable(searchText="", maxResults=100): Observable<InstanceVariable[]> {
    return this._http.get(env.contextPath + '/api/v2/public/instanceVariables?query=' + searchText + '&max=' +maxResults)
      .map(response => response.json() as InstanceVariable[])
  }

  searchInstanceVariableByVariableId(variableId, maxResults=100): Observable<InstanceVariable[]> {
    return this._http.get(env.contextPath + '/api/v2/public/variables/'+variableId+'/instanceVariables?max=' +maxResults)
      .map(response => response.json() as InstanceVariable[])
  }
}
