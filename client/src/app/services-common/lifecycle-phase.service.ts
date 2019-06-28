import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'



import { environment as env } from '../../environments/environment'

import { LifecyclePhase } from '../model2/lifecycle-phase'

@Injectable()
export class LifecyclePhaseService {

  constructor(
    private http: HttpClient
  ) { }

  getAll(): Observable<LifecyclePhase[]> {
    return this.http.get<LifecyclePhase[]>(env.contextPath + env.apiPath + '/lifecyclePhases');
  }

}
