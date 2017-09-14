import { Injectable } from '@angular/core'
import { Http, Headers, RequestOptions } from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'

import { environment as env} from '../../environments/environment'

import { Dataset } from '../model2/dataset'
import { GrowlMessageService } from '../services2/growl-message.service'

@Injectable()
export class DatasetService3 {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  search(searchText: string): Observable<Dataset[]> {
    return this.http.get(env.contextPath + '/api/v3/editor/datasets?query=' + searchText + '&max=50')
      .map(response => response.json() as Dataset[])
  }

  getAll(): Observable<Dataset[]> {
    return this.http.get(env.contextPath + '/api/v3/editor/datasets')
      .map(response => response.json() as Dataset[])
  }

  getDataset(id: string): Observable<Dataset> {
    return this.http.get(env.contextPath + '/api/v3/editor/datasets/' + id)
      .map(response => response.json() as Dataset)
  }

  save(dataset: Dataset): Observable<Dataset> {
    return this.saveDatasetInternal(dataset)
      .do(dataset => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.dataset.save.result.success')
      })
  }

  private saveDatasetInternal(dataset: Dataset): Observable<Dataset> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(env.contextPath + '/api/v3/editor/datasets', dataset, options)
      .map(response => response.json() as Dataset)
  }

  publish(dataset: Dataset): Observable<Dataset> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(env.contextPath + '/api/v3/editor/dataset-functions/publish?datasetId=' + dataset.id, {}, options)
      .map(response => response.json() as Dataset)
  }

  withdraw(dataset: Dataset): Observable<Dataset> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(env.contextPath + '/api/v3/editor/dataset-functions/withdraw?datasetId=' + dataset.id, {}, options)
      .map(response => response.json() as Dataset)
  }

  importDataset(event): Observable<Dataset> {
    return Observable.throw('Not implemented')
  }

}
