import { Injectable } from '@angular/core'
import {Headers, Http, RequestOptions} from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env } from '../../environments/environment'

import { Organization } from '../model2/organization'
import {GrowlMessageService} from "./growl-message.service";


@Injectable()
export class OrganizationService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  getAllOrganizations(): Observable<Organization[]> {
    return this.http.get(env.contextPath + env.apiPath + '/organizations')
      .map(response => response.json() as Organization[])
  }

  save(organization: Organization): Observable<Organization> {
    const path: string = env.contextPath + env.apiPath + '/organizations/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, organization, options)
      .map(response => response.json() as Organization)
      .do(organization => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.organization.save.result.success')
      })
  }
}
