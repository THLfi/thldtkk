import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env} from "../../environments/environment";

import { PersonInRole } from '../model/person-in-role';

@Injectable()
export class PersonInRoleService {
  constructor(private _http: Http) {
  }

  getAllPersonsInRoles(): Observable<PersonInRole[]> {
    return this._http.get(env.contextPath + '/api/personsInRoles')
      .map(response => response.json() as PersonInRole[]);
  }
}

