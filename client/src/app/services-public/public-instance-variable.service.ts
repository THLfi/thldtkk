import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/do'
import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { InstanceVariable } from '../model2/instance-variable'

@Injectable()
export class PublicInstanceVariableService {

  constructor(
    private http: Http
  ) { }

  get(datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const url = env.contextPath
      + '/api/v3/public/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId
    return this.http.get(url).map(response => response.json() as InstanceVariable)
  }

  getInstanceVariable(studyId:string, datasetId: string, instanceVariableId: string): Observable<InstanceVariable> {
    const url = env.contextPath
      + '/api/v3/public/studies/'
      + studyId 
      +'/datasets/'
      + datasetId
      + '/instanceVariables/'
      + instanceVariableId
    return this.http.get(url).map(response => response.json() as InstanceVariable)
  }

  search(searchText = '', max = 100): Observable<InstanceVariable[]> {
    const url = env.contextPath
      + '/api/v3/public/instanceVariables?query='
      + searchText
      + '&max='
      + max
    return this.http.get(url).map(response => response.json() as InstanceVariable[])
  }

  getInstanceVariableAsCsvExportPath(datasetId: string, encoding="ISO-8859-15"): string {
      const path: string = env.contextPath
      + '/api/v3/public/datasets/'
      + datasetId
      + '/instanceVariables.csv?'
      + 'encoding=' + encoding

      return path;
  }



}
