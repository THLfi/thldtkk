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

  getInstanceVariable(studyId: string, datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this.http.get(path)
      .map(response => response.json() as InstanceVariable)
  }

  saveInstanceVariable(studyId: string, datasetId: string, instanceVariable: InstanceVariable): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
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

  deleteInstanceVariable(studyId: string, datasetId: string, instanceVariableId: string): Observable<any> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.instanceVariable.delete.result.success')
      })
  }

  importInstanceVariablesAsCsv(studyId: string, datasetId: string, file: File, encoding: string, overwrite: boolean): Observable<any> {
    const formData: FormData = new FormData()
    formData.append('file', file, file.name)

    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables?overwrite='
      + overwrite
    const headers = new Headers({ 'Content-Type': 'text/csv;charset=' + encoding })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, file, options)
      .map(response => response.json() as InstanceVariable)
      .do(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceVariable.import.result.success')
      })
  }

  getInstanceVariableAsCsvExportPath(studyId: string, datasetId: string, encoding = 'ISO-8859-15'): string {
    return env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables.csv?'
      + 'encoding=' + encoding
  }

  search(searchText = '', max = 100): Observable<InstanceVariable[]> {
    const url = env.contextPath
      + env.apiPath
      + '/editor/instanceVariables?query='
      + searchText
      + '&max='
      + max
    return this.http.get(url).map(response => response.json() as InstanceVariable[])
  }

  getPreviousInstanceVariableId(studyId: string, datasetId: string, instanceVariableId: string): Observable<string> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId
      + '/previous'

    return this.http.get(path)
      .map(response => response.json() as string)
  }

  getNextInstanceVariableId(studyId: string, datasetId: string, instanceVariableId: string): Observable<string> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId
      + '/next'

    return this.http.get(path)
      .map(response => response.json() as string)
  }

  downloadExampleInstanceVariableCsv(): string {
    let encoding
    if (navigator && navigator.platform && navigator.platform.indexOf('Mac') > -1) {
      encoding = 'MacRoman'
    } else {
      encoding = 'ISO-8859-15'
    }
    return env.contextPath
      + env.apiPath
      + '/editor/exampleImportVariables.xls?'
      + 'encoding=' + encoding
  }
}
