import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Role } from '../model2/role'

@Injectable()
export class RoleService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private _http: Http
  ) { }

  getAll(): Observable<Role[]> {
    return this._http.get(env.contextPath + '/api/v2/roles')
      .map(response => response.json() as Role[])
  }

  getById(id: string): Observable<Role> {
    return this._http.get(env.contextPath + '/api/v2/roles/' + id)
      .map(response => response.json() as Role)
  }

  save(role: Role): Observable<Role> {
    const path: string = env.contextPath + '/api/v2/roles/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, role, options)
      .map(response => response.json() as Role)
      .do(role => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.role.save.result.success')
      })
  }

}
