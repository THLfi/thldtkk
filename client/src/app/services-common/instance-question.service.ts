import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { InstanceQuestion } from '../model2/instance-question'

@Injectable()
export class InstanceQuestionService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  searchQuestion(searchText: string, datasetId:string): Observable<InstanceQuestion[]> {
    return this.http.get(env.contextPath +
      '/api/v3/instanceQuestions/dataset/'
      + datasetId +
      '/?query=' + searchText + '&max=50')
      .map(response => response.json() as InstanceQuestion[])
  }

  save(question: InstanceQuestion): Observable<InstanceQuestion> {
    const path: string = env.contextPath + '/api/v3/instanceQuestions'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, question, options)
      .map(response => response.json() as InstanceQuestion)
      .do(question => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceQuestion.save.result.success')
      })
  }

}
