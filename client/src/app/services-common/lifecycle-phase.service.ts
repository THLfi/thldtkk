import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env } from '../../environments/environment'

import { LifecyclePhase } from '../model2/lifecycle-phase'

@Injectable()
export class LifecyclePhaseService {

  constructor(
    private http: Http
  ) { }

  getAll(): Observable<LifecyclePhase[]> {
    return this.http.get(env.contextPath + env.apiPath + '/lifecyclePhases')
      .map(response => response.json() as LifecyclePhase[])
  }

}
