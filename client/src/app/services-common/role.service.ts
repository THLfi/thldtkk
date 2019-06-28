import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { Role } from '../model2/role'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class RoleService {

  constructor(
    private http: HttpClient
  ) { }

  getAll(): Observable<Role[]> {
    return this.http.get<Role[]>(env.contextPath + env.apiPath + '/roles');
  }

}
