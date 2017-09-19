import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/do'
import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from '../services-common/growl-message.service'
import { InstanceVariable } from '../model2/instance-variable'

@Injectable()
export class EditorInstanceVariableService {

  constructor(
    private http: Http,
    private growlMessageService: GrowlMessageService
  ) { }

  getInstanceVariable(datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + '/api/v3/editor/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this.http.get(path)
      .map(response => response.json() as InstanceVariable)
  }

  saveInstanceVariable(datasetId: string, instanceVariable: InstanceVariable): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + '/api/v3/editor/datasets/'
      + datasetId
      + '/instanceVariables'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    if (instanceVariable.valueDomainType != 'described') {
      instanceVariable.quantity = null
      instanceVariable.unit = null
      instanceVariable.valueRangeMin = null
      instanceVariable.valueRangeMax = null
    }
    if (instanceVariable.valueDomainType != 'enumerated') {
      instanceVariable.codeList = null
    }

    return this.http.post(path, instanceVariable, options)
      .map(response => response.json() as InstanceVariable)
      .do(iv => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceVariable.save.result.success')
      })
  }

  deleteInstanceVariable(datasetId: string, instanceVariableId: string): Observable<any> {
    const path: string = env.contextPath
      + '/api/v3/editor/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.instanceVariable.delete.result.success')
      })
  }

  importInstanceVariablesAsCsv(datasetId: string, file: File, encoding: string): Observable<any> {
    const formData: FormData = new FormData()
    formData.append('file', file, file.name)

    const path: string = env.contextPath
      + '/api/v3/editor/datasets/'
      + datasetId
      + '/instanceVariables'
    const headers = new Headers({ 'Content-Type': 'text/csv;charset=' + encoding })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, file, options)
      .map(response => response.json() as InstanceVariable)
      .do(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceVariable.import.result.success')
      })
  }

  searchInstanceVariable(searchText="", maxResults=100, broadSearch=true, requirePublished=true): Observable<InstanceVariable[]> {
    return this.http.get(env.contextPath + '/api/v3/editor/instanceVariables?query=' + searchText + '&max=' +maxResults + '&broadSearch=' +broadSearch +'&requirePublished=' +requirePublished)
      .map(response => response.json() as InstanceVariable[])
  }

  searchInstanceVariableByVariableId(variableId, maxResults=100): Observable<InstanceVariable[]> {
    return this.http.get(env.contextPath + '/api/v3/editor/variables/'+variableId+'/instanceVariables?max=' +maxResults)
      .map(response => response.json() as InstanceVariable[])
  }

}
