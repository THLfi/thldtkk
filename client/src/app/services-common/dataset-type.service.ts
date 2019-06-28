import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'



import { environment as env } from '../../environments/environment'

import { DatasetType } from '../model2/dataset-type'

@Injectable()
export class DatasetTypeService {

  constructor(
    private http: HttpClient
  ) { }

  getAll(): Observable<DatasetType[]> {
    return this.http.get<DatasetType[]>(env.contextPath + env.apiPath + '/datasetTypes');
  }

}
