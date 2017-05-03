import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { Population } from "../model/population";

@Injectable()
export class PopulationService {

  constructor(
    private _http: Http
  ) {}

  savePopulation(population: Population): Observable<Population> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' });
    const options = new RequestOptions({ headers: headers });

    return this._http.post('../metadata-api/populations', population, options)
      .map(response => response.json() as Population);
  }

}
