import { Http } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { SystemRole } from '../model2/system-role'

@Injectable()
export class EditorSystemRoleService {

  constructor(
    private http: Http
  ) { }

  getAll(): Observable<SystemRole[]> {
    return this.http.get(env.contextPath + '/api/v3/systemRoles')
      .map(response => response.json() as SystemRole[])
  }

}
