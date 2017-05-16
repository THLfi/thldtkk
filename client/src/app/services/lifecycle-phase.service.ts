import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env} from "../../environments/environment";

import { LifecyclePhase } from "../model/lifecycle-phase";

@Injectable()
export class LifecyclePhaseService {

  constructor(
    private _http: Http
  ) {}

  saveLifecyclePhase(lifecyclePhase: LifecyclePhase): Observable<LifecyclePhase> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' });
    const options = new RequestOptions({ headers: headers });

    return this._http.post(env.contextPath + '/api/lifecyclePhases', lifecyclePhase, options)
      .map(response => response.json() as LifecyclePhase);
  }

}

