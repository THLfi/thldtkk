import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import {environment as env} from "../../environments/environment";

import {OrganizationUnit} from '../model2/organization-unit';

@Injectable()
export class OrganizationUnitService {
    constructor(private _http: Http) {
    }

    getAllOrganizationUnits(): Observable<OrganizationUnit[]> {
        return this._http.get(env.contextPath + '/api/v2/organizationUnits')
            .map(response => response.json() as OrganizationUnit[]);
    }
}


