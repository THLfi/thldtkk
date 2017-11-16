import { environment as env } from '../../environments/environment'
import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Study } from '../model2/study'

import 'rxjs/add/operator/map'

@Injectable()
export class PublicStudyService {

  constructor(
    private http: Http) {}

  getAll(): Observable<Study[]> {
    return this.http.get(env.contextPath + '/api/v3/public/studies')
      .map(response => response.json() as Study[])
  }

  getStudy(id: string): Observable<Study> {
    return this.http.get(env.contextPath + '/api/v3/public/studies/' + id)
      .map(response => response.json() as Study)
  }

}