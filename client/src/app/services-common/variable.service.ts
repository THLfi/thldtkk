
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { InstanceVariable } from '../model2/instance-variable'
import { Variable } from '../model2/variable'
import { Dataset } from '../model2/dataset'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class VariableService {

  constructor(
    private translateService : TranslateService,
    private nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private http: HttpClient
  ) { }

  search(searchText = ""): Observable<Variable[]> {
    return this.http.get<Variable[]>(env.contextPath + env.apiPath + '/variables?query=' + searchText + '&max=50');
  }

  get(variableId: string): Observable<Variable> {
    return this.http.get<Variable>(env.contextPath + env.apiPath + '/variables/' + variableId);
  }

  getAll(): Observable<Variable[]> {
    return this.http.get<Variable[]>(env.contextPath + env.apiPath + '/variables');
  }

  save(variable: Variable): Observable<Variable> {
    const path: string = env.contextPath + env.apiPath + '/variables'

    return this.http.post<Variable>(path, variable).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.variable.save.result.success')
      }))
  }

  delete(variableId: string): Observable<any> {
    const path: string = env.contextPath + env.apiPath + '/variables/' + variableId

    return this.http.delete(path).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.variable.delete.result.success')
      }))
  }

  getInstanceVariables(variableId: string, graph: string): Observable<InstanceVariable[]> {
    const url = env.contextPath
      + env.apiPath
      + '/' + graph + '/variables/'
      + variableId
      +'/instanceVariables'
    return this.http.get<InstanceVariable[]>(url);
  }

  initNew(): Variable {
    const variable = {
      id: null,
      prefLabel: null,
      description: null
    }
    this.nodeUtils.initLangValuesProperties(variable,
          [ 'prefLabel', 'description' ],
          [ this.translateService.currentLang ])
    return variable
  }

  getVariableDatasets(variable: Variable): Observable<Dataset[]> {
      const path: string = env.contextPath
        + env.apiPath + '/variables/'
        + variable.id
        + '/datasets'

      return this.http.get<Dataset[]>(path);
    }

    getVariableInstanceVariables(variable: Variable): Observable<InstanceVariable[]> {
        const path: string = env.contextPath
          + env.apiPath
          + '/editor/variables/'
          + variable.id
          + '/instanceVariables'

        return this.http.get<InstanceVariable[]>(path);
      }
}
