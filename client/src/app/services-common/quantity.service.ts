
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Quantity } from '../model2/quantity'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class QuantityService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: HttpClient
  ) { }

  getAll(): Observable<Quantity[]> {
    return this.http.get<Quantity[]>(env.contextPath + env.apiPath + '/quantities');
  }

  save(quantity: Quantity): Observable<Quantity> {
    const path: string = env.contextPath + env.apiPath + '/quantities'

    return this.http.post<Quantity>(path, quantity).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.quantity.save.result.success')
      }))
  }

}
