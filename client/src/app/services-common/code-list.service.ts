import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { CodeList } from '../model2/code-list'
import { GrowlMessageService } from './growl-message.service'

@Injectable()
export class CodeListService3 {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  search(query: string): Observable<CodeList[]> {
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

}
