import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { Unit } from '../model2/unit'

@Injectable()
export class UnitService {

  constructor(
    private _http: Http
  ) { }

  getAll(): Observable<Unit[]> {
    return this._http.get(env.contextPath + '/api/v2/units')
      .map(response => response.json() as Unit[])
  }

  getById(id: string): Observable<Unit> {
    return this._http.get(env.contextPath + '/api/v2/units/' + id)
      .map(response => response.json() as Unit)
  }

  save(unit: Unit): Observable<Unit> {
    const path: string = env.contextPath + '/api/v2/units/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, unit, options)
      .map(response => response.json() as Unit)
  }

}
