import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'

import { Dataset } from "../model2/dataset";

@Injectable()
export class DatasetService {

  constructor(
    private _http: Http
  ) { }

  getAllDatasets(): Observable<Dataset[]> {
    return this._http.get('../metadata-api/v2/datasets')
      .map(response => response.json() as Dataset[]);
  }

}
