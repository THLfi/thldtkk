import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { environment as env} from '../../environments/environment'

import { InstanceVariable } from '../model2/instance-variable'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class PublicInstanceVariableService {

  constructor(
    private http: HttpClient
  ) { }

  getInstanceVariable(studyId:string, datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const url = env.contextPath
      + env.apiPath
      + '/public/studies/'
      + studyId
      +'/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId
    return this.http.get<InstanceVariable>(url);
  }

  getInstanceVariableWithSelect(instanceVariableId: string, select: string[]) {
    const url = env.contextPath
      + env.apiPath
      + '/public/instanceVariables/'
      + instanceVariableId
    return this.http.get<InstanceVariable>(url, {
      params: {
        select: JSON.stringify(select)
      }
    });
  }

  search(searchText = '', max = 100): Observable<InstanceVariable[]> {
    const url = env.contextPath
      + env.apiPath
      + '/public/instanceVariables?query='
      + searchText
      + '&max='
      + max
    return this.http.get<InstanceVariable[]>(url);
  }

  getInstanceVariableAsCsvExportPath(studyId: string, datasetId: string, encoding = 'ISO-8859-15'): string {
    return env.contextPath
      + env.apiPath
      + '/public/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables.csv?'
      + 'encoding=' + encoding
  }

  getNextInstanceVariableId(studyId: string, datasetId: string, instanceVariableId: string): Observable<string> {
    const path: string = env.contextPath
      + env.apiPath
      + '/public/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId
      + '/next'

    return this.http.get<string>(path);
  }
}
