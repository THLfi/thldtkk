import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
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

}
