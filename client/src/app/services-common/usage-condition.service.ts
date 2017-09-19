import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'

import { environment as env } from '../../environments/environment'

import { UsageCondition } from '../model2/usage-condition'

@Injectable()
export class UsageConditionService {

  constructor(
    private http: Http
  ) {}

  getAll(): Observable<UsageCondition[]> {
    return this.http.get(env.contextPath + '/api/v3/usageConditions')
      .map(response => response.json() as UsageCondition[])
  }

}
