import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'


import { environment as env } from '../../environments/environment'

import { UsageCondition } from '../model2/usage-condition'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UsageConditionService {

  constructor(
    private http: HttpClient
  ) {}

  getAll(): Observable<UsageCondition[]> {
    return this.http.get<UsageCondition[]>(env.contextPath + env.apiPath + '/usageConditions');
  }

}
