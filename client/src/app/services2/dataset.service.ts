import { Injectable } from '@angular/core'
import { Http } from '@angular/http'
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

  getDataset(id: string): Observable<Dataset> {
    return this._http.get(env.contextPath + '/api/v2/datasets/' + id)
      .map(response => response.json() as Dataset)
  }

}
