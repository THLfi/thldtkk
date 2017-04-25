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
        return this._http.get('../metadata-api/termed/types/InstanceVariable/nodes')
            .map(response => response.json() as InstanceVariable[]);
    }

    getInstanceVariable(id: String): Observable<InstanceVariable> {
        return this.getAllInstanceVariables()
            .map(instanceVariables => {
                for (let instanceVariable of instanceVariables) {
                    if (id === instanceVariable.id) {
                        return instanceVariable;
                    }
                }
            });
    }

}
