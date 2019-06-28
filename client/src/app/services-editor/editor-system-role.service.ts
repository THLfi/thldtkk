import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { SystemRole } from '../model2/system-role'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class EditorSystemRoleService {

  constructor(
    private http: HttpClient
  ) { }

  getAll(): Observable<SystemRole[]> {
    return this.http.get<SystemRole[]>(env.contextPath + env.apiPath + '/systemRoles');
  }

}
