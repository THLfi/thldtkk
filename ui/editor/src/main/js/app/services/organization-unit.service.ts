import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { OrganizationUnit } from '../model/organization-unit';

@Injectable()
export class OrganizationUnitService {
  constructor(private _http: Http) {
  }

  getAllOrganizationUnits(): Observable<OrganizationUnit[]> {
    return this._http.get('../metadata-api/organizationUnits')
      .map(response => response.json() as OrganizationUnit[]);
  }
}

