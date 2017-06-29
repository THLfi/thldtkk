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

  getDatasets(organizationId: string, query: string): Observable<Dataset[]> {
    return this._http.get(env.contextPath + 
      '/api/v2/datasets?organizationId=' + organizationId + '&query=' + query)
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

    importDataset(event): Observable<Dataset> {
         let fileList: FileList = event.target.files;
         if (fileList.length > 0) {
             let file: File = fileList[0];
             let formData: FormData = new FormData();
             formData.append('uploadFile', file, file.name);
             let headers = new Headers();
             headers.append('Accept', 'application/json');
             let options = new RequestOptions({headers: headers});
             return this._http.post('/api/v2/datasets/xml-import', formData, options)
                 .map(res => res.json() as Dataset)
         }
     }

     searchDataset(searchText: string): Observable<Dataset[]> {
         return this._http.get(env.contextPath + '/api/v2/datasets?query=' + searchText + '&max=50')
           .map(response => response.json() as Dataset[])
     }
}
