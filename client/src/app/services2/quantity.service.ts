import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { HttpMessageHelper } from '../utils/http-message-helper'
import { Quantity } from '../model2/quantity'

@Injectable()
export class QuantityService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private _http: Http
  ) { }

  getAll(): Observable<Quantity[]> {
    return this._http.get(env.contextPath + '/api/v2/quantities')
      .map(response => response.json() as Quantity[])
  }

  getById(id: string): Observable<Quantity> {
    return this._http.get(env.contextPath + '/api/v2/quantities/' + id)
      .map(response => response.json() as Quantity)
  }

  save(quantity: Quantity): Observable<Quantity> {
    const path: string = env.contextPath + '/api/v2/quantities/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, quantity, options)
      .map(response => response.json() as Quantity)
      .catch(error => {
        this.growlMessageService.buildAndShowMessage('error',
          'operations.common.save.result.fail',
          HttpMessageHelper.getErrorMessageByStatusCode(error.status))
        return Observable.throw(error)
      })
      .do(quantity => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.quantity.save.result.success')
      })
  }

}
