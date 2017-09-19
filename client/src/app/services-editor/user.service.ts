import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { Organization } from '../model2/organization'

@Injectable()
export class UserService {

  constructor(
    private http: Http
  ) { }

  isUserAdmin(): Observable<boolean> {
    return this.http.post(env.contextPath + '/api/v2/user-functions/is-current-user-admin', {})
      .catch(error => {
        if (error.status === 401 ||error.status === 403) {
          return Observable.of({
            json: () => false
          })
        }
        else {
          return Observable.throw(error)
        }
      })
      .map(response => response.json() as boolean)
  }

  getUserOrganizations(): Observable<Organization[]> {
    return this.http.post(env.contextPath + '/api/v2/user-functions/list-current-user-organizations', {})
      .map(response => response.json() as Organization[])
  }

}
