import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { DataSet } from '../model/data-set';
import { InstanceVariable } from '../model/instance-variable';
import { Organization } from '../model/organization';
import { OrganizationUnit } from '../model/organization-unit';
import { PersonInRole } from "../model/person-in-role";
import { Population } from "../model/population";
import { UsageCondition } from "../model/usage-condition";
import { LifecyclePhase } from "../model/lifecycle-phase";

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

    getLifecyclePhases(datasetId: String): Observable<LifecyclePhase[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/lifecyclePhases')
            .map(response => response.json() as LifecyclePhase[]);
    }

    getDataSetUsageCondition(datasetId: String): Observable<UsageCondition[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/usageConditions')
            .map(response => response.json() as UsageCondition[]);
    }

    getDataSetOrganizationUnits(datasetId: String): Observable<OrganizationUnit[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/ownerOrganizationUnits')
            .map(response => response.json() as OrganizationUnit[]);
    }

    getDataSetPersonsInRoles(datasetId: String): Observable<PersonInRole[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/personsInRoles')
            .map(response => response.json() as PersonInRole[]);
    }

    getDataSetInstanceVariables(datasetId: String): Observable<InstanceVariable[]> {
        return this._http.get('../metadata-api/datasets/' + datasetId + '/instanceVariables')
            .map(response => response.json() as InstanceVariable[]);
    }

    saveDataSet(dataSet: DataSet): Observable<DataSet> {
      const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' });
      const options = new RequestOptions({ headers: headers });

      return this._http.post('../metadata-api/datasets', dataSet, options)
        .map(response => response.json() as DataSet);
    }

    publishDataSet(dataSet: DataSet): Observable<DataSet> {
      return this.changePublishedValue(dataSet, 'true')
    }

    private changePublishedValue(dataSet: DataSet, value: string): Observable<DataSet> {
      if (!dataSet.properties['published']) {
        dataSet.properties['published'] = [
          {
            lang: '',
            value: null,
            regex: '^(true|false)$'
          }
        ]
      }

      dataSet.properties['published'][0].value = value

      return this.saveDataSet(dataSet)
    }

    unpublishDataSet(dataSet: DataSet): Observable<DataSet> {
      return this.changePublishedValue(dataSet, 'false')
    }
}
