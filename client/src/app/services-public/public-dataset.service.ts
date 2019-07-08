import { Injectable } from '@angular/core'


import { environment as env} from '../../environments/environment'
import { Observable } from 'rxjs'
import { Dataset } from '../model2/dataset'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class PublicDatasetService {

  constructor(
    private http: HttpClient
  ) { }

  getDataset(studyId: string, datasetId: string): Observable<Dataset> {
    return this.http.get<Dataset>(env.contextPath
      + env.apiPath
      + '/public/studies/'
      + studyId
      + '/datasets/'
      + datasetId);
  }

  getDatasetWithSelect(datasetId: string, select: string[]) {
    return this.http.get<Dataset>(env.contextPath
      + env.apiPath
      + '/public/datasets/'
      + datasetId, {
        params: {
          select: JSON.stringify(select)
        }
    });
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
      + env.apiPath
      + '/public/datasets?query='
      + searchText
      + '&sort='
      + sort
      + '&organizationId='
      + organizationId
      + '&max='
      + max
    return this.http.get<Dataset[]>(url);
  }

  getRecentDatasets(max = 10): Observable<Dataset[]> {
    return this.searchInternal('', '', 'lastModifiedDate.sortable', max)
  }

}
