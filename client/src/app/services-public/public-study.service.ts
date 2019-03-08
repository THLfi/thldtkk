import { environment as env } from '../../environments/environment'
import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Study } from '../model2/study'

import 'rxjs/add/operator/map'

@Injectable()
export class PublicStudyService {

  constructor(
    private http: Http) {}

  getAll(): Observable<Study[]> {
    return this.http.get(env.contextPath + env.apiPath + '/public/studies')
      .map(response => response.json() as Study[])
  }

  getStudy(id: string): Observable<Study> {
    return this.http.get(env.contextPath + env.apiPath + '/public/studies/' + id)
      .map(response => response.json() as Study)
  }

  private searchInternal(searchText: string, organizationId: string, sort: string, max: number): Observable<Study[]> {
    const url = env.contextPath
      + env.apiPath
      + '/public/studies?query='
      + searchText
      + '&sort='
      + sort
      + '&organizationId='
      + organizationId
      + '&max='
      + max
    return this.http.get(url).map(response => response.json() as Study[])
  }

  getRecentStudies(max=10): Observable<Study[]> {
    return this.searchInternal('','', 'lastModifiedDate.sortable+desc', max)
  }

  search(searchText: string, organizationId: string): Observable<Study[]> {
    return this.searchInternal(searchText, organizationId, '', -1)
  }

}
