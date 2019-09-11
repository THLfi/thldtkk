import { environment as env } from '../../environments/environment'
import { Injectable } from '@angular/core'
import { Study } from '../model2/study'


import { HttpClient } from '@angular/common/http';

@Injectable()
export class PublicStudyService {

  constructor(
    private http: HttpClient
  ) {}

  getStudy(id: string) {
    return this.http.get<Study>(env.contextPath + env.apiPath + '/public/studies/' + id);
  }

  getStudyWithSelect(id: string, select: string[]) {
    let path = env.contextPath + env.apiPath + '/public/studies/' + id;
    if (select && select.length) {
      path += `?select=${encodeURIComponent(JSON.stringify(select))}`;
    }
    return this.http.get<Study>(path);
  }

  private searchInternal(
    searchText: string,
    organizationId: string,
    sort: string,
    max: number,
    select: string[]
  ) {
    const params = {
      query: searchText,
      sort: sort,
      organizationId: organizationId,
      max: max,
      select: JSON.stringify(select)
    };
    const paramString = Object.keys(params).map(function(k) {
      return encodeURIComponent(k) + "=" + encodeURIComponent(params[k]);
    }).join('&')
    return this.http.get<Study[]>(env.contextPath + env.apiPath + '/public/studies' + `?${paramString}`);
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
      'properties.prefLabel.sortable',
      -1,
      ['properties.prefLabel']
    );
  }

}
