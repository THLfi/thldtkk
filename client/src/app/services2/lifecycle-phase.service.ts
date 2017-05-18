import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import {environment as env} from "../../environments/environment";

import {LifecyclePhase} from '../model2/lifecycle-phase';

@Injectable()
export class LifecyclePhaseService {
    constructor(private _http: Http) {
    }

    getAllLifecyclePhases(): Observable<LifecyclePhase[]> {
        return this._http.get(env.contextPath + '/api/v2/lifecyclePhases')
            .map(response => response.json() as LifecyclePhase[]);
    }
}



