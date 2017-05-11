import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { PersonInRole } from '../model/person-in-role';

@Injectable()
export class PersonInRoleService {
  constructor(private _http: Http) {
  }

  getAllPersonsInRoles(): Observable<PersonInRole[]> {
    return this._http.get('../metadata-api/personsInRoles')
      .map(response => response.json() as PersonInRole[]);
  }
}

