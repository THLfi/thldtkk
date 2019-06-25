import { environment as env } from '../../environments/environment'
import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { Study } from '../model2/study'

import 'rxjs/add/operator/map'

@Injectable()
export class PublicStudyService {

  constructor(
    private http: Http
  ) {}

  getStudy(id: string) {
    return this.http.get(env.contextPath + env.apiPath + '/public/studies/' + id)
      .map(response => response.json() as Study)
  }

  getStudyWithSelect(id: string, select: string[]) {
    return this.http.get(env.contextPath + env.apiPath + '/public/studies/' + id, {search: {
      select: JSON.stringify(select)
    }})
      .map(response => response.json() as Study)
  }

  private searchInternal(
    searchText: string,
    organizationId: string,
    sort: string,
    max: number,
    select: string[]
  ) {
    return this.http.get(env.contextPath + env.apiPath + '/public/studies', {search: {
      query: searchText,
      sort: sort,
      organizationId: organizationId,
      max: max,
      select: JSON.stringify(select)
    }})
      .map(response => response.json() as Study[]);
  }

  getRecentStudies(max=10) {
    return this.searchInternal(
      '',
      '',
      'lastModifiedDate.sortable desc',
      max,
      ['properties.prefLabel']
    );
  }

  search(searchText: string, organizationId: string) {
    return this.searchInternal(
      searchText,
      organizationId,
      '',
      -1,
      ['properties.prefLabel']
    );
  }

}
