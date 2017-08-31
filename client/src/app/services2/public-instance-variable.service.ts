import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs'
import { ObservableInput } from 'rxjs/Observable'
import 'rxjs/add/operator/do'
import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { InstanceVariable } from '../model2/instance-variable';
import { HttpMessageHelper } from "../utils/http-message-helper";

@Injectable()
export class PublicInstanceVariableService {

  constructor(
    private _http: Http,
    private growlMessageService: GrowlMessageService
  ) { }

  getInstanceVariable(datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + '/api/v2/public/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this._http.get(path)
      .map(response => response.json() as InstanceVariable)
      .catch(error => this.handleHttpError(error, 'operations.instanceVariable.get.result.fail'))
  }

  private handleHttpError(error: any, summaryMessageKey: string): ObservableInput<any> {
    this.growlMessageService.buildAndShowMessage('error', summaryMessageKey,
      HttpMessageHelper.getErrorMessageByStatusCode(error.status))
    return Observable.throw(error)
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
