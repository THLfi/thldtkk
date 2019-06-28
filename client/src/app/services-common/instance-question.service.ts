
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { InstanceQuestion } from '../model2/instance-question'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class InstanceQuestionService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: HttpClient
  ) { }

  searchQuestion(searchText: string, studyId: string, datasetId: string): Observable<InstanceQuestion[]> {
    return this.http.get<InstanceQuestion[]>(env.contextPath
      + env.apiPath
      + '/instanceQuestions/studies/'
      + studyId
      + '/datasets/'
      + datasetId
      + '/?query=' + searchText + '&max=50');
  }

  save(question: InstanceQuestion): Observable<InstanceQuestion> {
    const path: string = env.contextPath + env.apiPath + '/instanceQuestions'

    return this.http.post<InstanceQuestion>(path, question).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.instanceQuestion.save.result.success')
      }))
  }

}
