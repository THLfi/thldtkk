import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { NodeUtils } from '../utils/node-utils'
import { CodeItem } from '../model2/code-item'
import { CodeList } from '../model2/code-list'
import { InstanceVariable } from '../model2/instance-variable'
import { GrowlMessageService } from './growl-message.service'

@Injectable()
export class CodeListService3 {

  constructor(
    private translateService : TranslateService,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private http: Http
  ) { }

  search(query = ""): Observable<CodeList[]> {
    return this.http.get(env.contextPath + '/api/v3/codeLists?query=' + query)
      .map(response => response.json() as CodeList[])
  }

  getAll(): Observable<CodeList[]> {
    return this.search("")
  }

  save(codeList: CodeList): Observable<CodeList> {
    const path: string = env.contextPath + '/api/v3/codeLists'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    if ('external' == codeList.codeListType) {
      codeList.codeItems = []
    }
    else if ('internal' == codeList.codeListType) {
      codeList.referenceId = null
    }

    return this.http.post(path, codeList, options)
      .map(response => response.json() as CodeList)
      .do(codeList => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.codeList.save.result.success')
      })
  }

  initNew(): CodeList {
    const codeList = {
      id: null,
      codeListType: null,
      referenceId: null,
      prefLabel: null,
      description: null,
      owner: null,
      codeItems: []
    }
    this.nodeUtils.initLangValuesProperties(codeList,
              [ 'prefLabel', 'description', 'owner' ],
              [ this.translateService.currentLang ])
    return codeList;
  }

  initNewCodeItem(): CodeItem {
    const newCodeItem = {
        id: null,
        code: null,
        prefLabel: null
      }
      this.nodeUtils.initLangValuesProperties(newCodeItem, ['prefLabel'], [ this.translateService.currentLang ])
      return newCodeItem
  }

  getCodeListInstanceVariables(codeListId: string): Observable<InstanceVariable[]> {
    const url = env.contextPath
          + '/api/v3/editor/codeLists/'
          + codeListId
          +'/instanceVariables'
    return this.http.get(url).map(response => response.json() as InstanceVariable[])
  }

  delete(codeListId: string): Observable<any>{
    const path: string = env.contextPath + '/api/v3/codeLists/' + codeListId

      return this.http.delete(path)
          .map(response => response.json())
          .do(() => {
            this.growlMessageService.buildAndShowMessage('info', 'operations.common.delete.result.success')
      })
  }
}
