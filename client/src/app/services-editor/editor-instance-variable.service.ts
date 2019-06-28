
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'




import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from '../services-common/growl-message.service'
import { InstanceVariable } from '../model2/instance-variable'
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class EditorInstanceVariableService {

  constructor(
    private http: HttpClient,
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

    return this.http.get<InstanceVariable>(path);
  }

  saveInstanceVariable(studyId: string, datasetId: string, instanceVariable: InstanceVariable): Observable<InstanceVariable> {
    const path: string = env.contextPath
      + env.apiPath
      + '/editor/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables'

    if (instanceVariable.valueDomainType != 'described') {
      instanceVariable.quantity = null
      instanceVariable.unit = null
      instanceVariable.valueRangeMin = null
      instanceVariable.valueRangeMax = null
    }
    if (instanceVariable.valueDomainType != 'enumerated') {
      instanceVariable.codeList = null
    }

    return this.http.post<InstanceVariable>(path, instanceVariable).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceVariable.save.result.success')
      }))
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

    return this.http.delete(path).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.instanceVariable.delete.result.success')
      }))
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

    let headers = new HttpHeaders();
    headers = headers.set('Content-Type', 'text/csv;charset=' + encoding);

    return this.http.post(path, file, { headers: headers }).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceVariable.import.result.success')
      }))
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
    return this.http.get<InstanceVariable[]>(url);
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

    return this.http.get<string>(path);
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

    return this.http.get<string>(path);
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
