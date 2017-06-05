import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import {environment as env} from "../../environments/environment";

import {DatasetType} from '../model2/dataset-type';

@Injectable()
export class DatasetTypeService {
    constructor(private _http: Http) {
    }

    getDatasetTypes(): Observable<DatasetType[]> {
        return this._http.get(env.contextPath + '/api/v2/datasetTypes')
            .map(response => response.json() as DatasetType[]);
    }

    getDatasetType(datasetTypeId: string): Observable<DatasetType> {
        return this._http.get(env.contextPath 
            + '/api/v2/datasetTypes/'
            + datasetTypeId)
            .map(response => response.json() as DatasetType);
    }

}

