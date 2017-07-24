import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { HttpMessageHelper } from '../utils/http-message-helper'
import { InstanceQuestion } from '../model2/instance-question'

@Injectable()
export class InstanceQuestionService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private _http: Http
  ) { }

  searchQuestion(searchText: string, datasetId:string): Observable<InstanceQuestion[]> {
    return this._http.get(env.contextPath + 
      '/api/v2/instanceQuestions/dataset/'
      + datasetId +
      '/?query=' + searchText + '&max=50')
      .map(response => response.json() as InstanceQuestion[])
  }

  save(question: InstanceQuestion): Observable<InstanceQuestion> {
    const path: string = env.contextPath + '/api/v2/instanceQuestions/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, question, options)
      .map(response => response.json() as InstanceQuestion)
      .catch(error => {
        this.growlMessageService.buildAndShowMessage('error',
          'operations.common.save.result.fail.summary',
          HttpMessageHelper.getErrorMessageByStatusCode(error.status))
        return Observable.throw(error)
      })
      .do(question => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceQuestion.save.result.success')
      })
  }

}