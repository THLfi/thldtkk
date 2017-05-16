import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'

import { environment as env} from "../../environments/environment";

import { UsageCondition } from "../model/usage-condition";

@Injectable()
export class UsageConditionService {
  constructor(
    private _http: Http
  ) { }

  getAllUsageConditions(): Observable<UsageCondition[]> {
    return this._http.get(env.contextPath + '/api/usageconditions?max=-1')
      .map(response => response.json() as UsageCondition[]);
  }
}
