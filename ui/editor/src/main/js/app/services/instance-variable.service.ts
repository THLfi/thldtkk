import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { InstanceVariable } from "../model/instance-variable";

@Injectable()
export class InstanceVariableService {
    constructor(
        private _http: Http
    ) {}

    getAllInstanceVariables(): Observable<InstanceVariable[]> {
        return this._http.get('../metadata-api/instanceVariables')
            .map(response => response.json() as InstanceVariable[]);
    }

    getInstanceVariable(id: String): Observable<InstanceVariable> {
        return this._http.get('../metadata-api/instanceVariables/' + id)
            .map(response => response.json() as InstanceVariable);
    }

  updateInstanceVariable(instanceVariable: InstanceVariable): Observable<InstanceVariable> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' });
    const options = new RequestOptions({ headers: headers });

    return this._http.post('../metadata-api/instanceVariables', instanceVariable, options)
      .map(response => response.json() as InstanceVariable);
  }

  remove(instanceVariable: InstanceVariable): Observable<any> {
    return this._http.delete('../metadata-api/instanceVariables/' + instanceVariable.id);
  }

}
