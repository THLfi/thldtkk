import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env} from "../../environments/environment";

import { Role } from '../model/role';

@Injectable()
export class RoleService {
  constructor(private _http: Http) {
  }

  getRole(roleId: String): Observable<Role> {
    return this._http.get(env.contextPath + '/api/roles/' + roleId )
      .map(response => response.json() as Role);
  }
}

