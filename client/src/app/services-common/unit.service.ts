import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Unit } from '../model2/unit'

@Injectable()
export class UnitService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  getAll(): Observable<Unit[]> {
    return this.http.get(env.contextPath + env.apiPath + '/units')
      .map(response => response.json() as Unit[])
  }

  save(unit: Unit): Observable<Unit> {
    const path: string = env.contextPath + env.apiPath + '/units/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, unit, options)
      .map(response => response.json() as Unit)
      .do(unit => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.unit.save.result.success')
      })
  }

}
