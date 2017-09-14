import { Injectable } from '@angular/core'
import { Http } from '@angular/http'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'
import { Observable } from 'rxjs'
import { Dataset } from '../model2/dataset'

@Injectable()
export class PublicDatasetService {

  constructor(
    private http: Http
  ) { }

  get(datasetId: string): Observable<Dataset> {
    const url = env.contextPath
      + '/api/v3/public/datasets/'
      + datasetId
    return this.http.get(url).map(response => response.json() as Dataset)
  }

  search(searchText: string, organizationId?: string, sort?: string, max?: number): Observable<Dataset[]> {
    return this.searchInternal(
      searchText,
      organizationId ? organizationId : '',
      sort ? sort : 'properties.prefLabel.sortable',
      max ? max : -1
    )
  }

  private searchInternal(searchText: string, organizationId: string, sort: string, max: number): Observable<Dataset[]> {
    const url = env.contextPath
      + '/api/v3/public/datasets?query='
      + searchText
      + '&sort='
      + sort
      + '&organizationId='
      + organizationId
      + '&max='
      + max
    return this.http.get(url).map(response => response.json() as Dataset[])
  }

  getRecentDatasets(max = 10): Observable<Dataset[]> {
    return this.searchInternal('', '', 'lastModifiedDate.sortable', max)
  }

}
