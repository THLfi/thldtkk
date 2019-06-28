
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Unit } from '../model2/unit'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UnitService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: HttpClient
  ) { }

  getAll(): Observable<Unit[]> {
    return this.http.get<Unit[]>(env.contextPath + env.apiPath + '/units');
  }

  save(unit: Unit): Observable<Unit> {
    const path: string = env.contextPath + env.apiPath + '/units/'

    return this.http.post<Unit>(path, unit).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.unit.save.result.success')
      }))
  }

}
