import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs"
import 'rxjs/add/operator/map'


import { environment as env} from '../../environments/environment'

import { Dataset } from '../model2/dataset'

@Injectable()
export class DatasetService {

  constructor(
    private _http: Http
  ) { }

  getAllDatasets(): Observable<Dataset[]> {
    return this._http.get(env.contextPath + '/api/v2/datasets')
      .map(response => response.json() as Dataset[])
  }

  getDatasets(organizationId: string): Observable<Dataset[]> {
    return this._http.get(env.contextPath + 
      '/api/v2/datasets?organizationId=' + organizationId)
      .map(response => response.json() as Dataset[])
  }

  getDataset(id: string): Observable<Dataset> {
    return this._http.get(env.contextPath + '/api/v2/datasets/' + id)
      .map(response => response.json() as Dataset)
  }

    saveDataset(dataset: Dataset): Observable<Dataset> {
      const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' });
      const options = new RequestOptions({ headers: headers });

      return this._http.post(env.contextPath + '/api/v2/datasets', dataset, options)
        .map(response => response.json() as Dataset);
    }

   publishDataSet(dataset: Dataset): Observable<Dataset> {
      dataset.published = true;
      return this.saveDataset(dataset);
    }

    unpublishDataSet(dataset: Dataset): Observable<Dataset> {
      dataset.published = false;
      return this.saveDataset(dataset);
    }

}
