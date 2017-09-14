import { Http } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { Role } from '../model2/role'

@Injectable()
export class RoleService3 {

  constructor(
    private http: Http
  ) { }

  getAll(): Observable<Role[]> {
    return this.http.get(env.contextPath + '/api/v3/roles')
      .map(response => response.json() as Role[])
  }

}
