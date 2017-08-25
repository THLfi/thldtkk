import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { HttpMessageHelper } from '../utils/http-message-helper'
import { Person } from '../model2/person'

@Injectable()
export class PersonService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private _http: Http
  ) { }

  getAll(): Observable<Person[]> {
    return this._http.get(env.contextPath + '/api/v2/persons')
      .map(response => response.json() as Person[])
  }

  getById(id: string): Observable<Person> {
    return this._http.get(env.contextPath + '/api/v2/persons/' + id)
      .map(response => response.json() as Person)
  }

  save(person: Person): Observable<Person> {
    const path: string = env.contextPath + '/api/v2/persons/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this._http.post(path, person, options)
      .map(response => response.json() as Person)
      .catch(error => {
        this.growlMessageService.buildAndShowMessage('error',
          'operations.common.save.result.fail.summary',
          HttpMessageHelper.getErrorMessageByStatusCode(error.status))
        return Observable.throw(error)
      })
      .do(role => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.person.save.result.success')
      })
  }

  initNew(): Person {
    return {
      id: null,
      firstName: null,
      lastName: null,
      email: null,
      phone: null
    }
  }

}
