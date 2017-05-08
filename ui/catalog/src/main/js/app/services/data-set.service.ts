import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { DataSet } from '../model/data-set';
import { Organization } from '../model/organization';
import { InstanceVariable } from '../model/instance-variable';
import { LifecyclePhase } from "../model/lifecycle-phase";
import { Population } from "../model/population";
import { UsageCondition } from "../model/usage-condition";

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

  getDataSetPopulations(datasetId: String): Observable<Population[]> {
    return this._http.get('../metadata-api/datasets/' + datasetId + '/populations')
      .map(response => response.json() as Population[]);
  }
  
  getDataSetLifecyclePhases(datasetId: String): Observable<LifecyclePhase[]> {
    return this._http.get('../metadata-api/datasets/' + datasetId + '/lifecyclePhases')
      .map(response => response.json() as LifecyclePhase[]);
  }

  getDataSetUsageCondition(datasetId: String): Observable<UsageCondition[]> {
    return this._http.get('../metadata-api/datasets/' + datasetId + '/usageConditions')
      .map(response => response.json() as UsageCondition[]);
  }

  getDataSetInstanceVariables(datasetId: String): Observable<InstanceVariable[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/instanceVariables')
            .map(response => response.json() as InstanceVariable[]);
    }

}
