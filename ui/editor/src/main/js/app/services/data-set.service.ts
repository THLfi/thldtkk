import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { DataSet } from '../model/data-set';
import { InstanceVariable } from '../model/instance-variable';
import { Organization } from '../model/organization';

@Injectable()
export class DataSetService {
    constructor(
        private _http: Http
    ) {}

    getAllDataSets(): Observable<DataSet[]> {
        return this._http.get('../metadata-api/datasets?max=-1')
            .map(response => response.json() as DataSet[]);
    }

    getDataSet(datasetId: String): Observable<DataSet> {
        return this._http.get('../metadata-api/datasets/' + datasetId)
            .map(response => response.json() as DataSet);
    }

    getDataSetOwners(datasetId: String): Observable<Organization[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/owners')
            .map(response => response.json() as Organization[]);
    }

    getDataSetInstanceVariables(datasetId: String): Observable<InstanceVariable[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/instanceVariables')
            .map(response => response.json() as InstanceVariable[]);
    }

    updateDataSet(dataSet: DataSet): Observable<DataSet> {
        const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' });
        const options = new RequestOptions({ headers: headers });

        return this._http.post('../metadata-api/datasets/' + dataSet.id, dataSet, options)
            .map(response => response.json() as DataSet);
    }
}
