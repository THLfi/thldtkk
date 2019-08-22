import { Injectable } from '@angular/core'

import { environment as env} from '../../environments/environment'

import { Role } from '../model2/role'
import { HttpClient } from '@angular/common/http';
import {RoleAssociation} from '../model2/role-association';
import {map} from 'rxjs/operators';

@Injectable()
export class RoleService {

  constructor(
    private http: HttpClient
  ) { }

  getAll() {
    return this.http.get<Role[]>(env.contextPath + env.apiPath + '/roles');
  }

  getAllByAssociation(association: RoleAssociation) {
    return this.getAll().pipe(
      map(roles => roles.filter(role => role.associations.includes(association)))
    );
  }
}
