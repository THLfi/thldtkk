import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { DataSet } from '../model/data-set';

@Injectable()
export class DataSetService {
    constructor(
        private _http: Http
    ) {}

    getAllDataSets(): Observable<DataSet[]> {
        return this._http.get('http://localhost:8080/api-metadata/termed/datasets?max=-1')
            .map(response => response.json() as DataSet[]);
    }

    getDataSet(dataSetId: String): Observable<DataSet> {
        return this.getAllDataSets()
            .map(dataSets => {
                for (let dataSet of dataSets) {
                    if (dataSetId === dataSet.id) {
                        return dataSet;
                    }
                }
            });
    }
}
