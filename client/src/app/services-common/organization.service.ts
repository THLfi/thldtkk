import { Injectable } from '@angular/core'
import { Http } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env } from '../../environments/environment'

import { Organization } from '../model2/organization'

@Injectable()
export class OrganizationService {

  constructor(
    private http: Http
  ) { }

  getAllOrganizations(): Observable<Organization[]> {
    return this.http.get(env.contextPath + env.apiPath + '/organizations')
      .map(response => response.json() as Organization[])
  }

}
