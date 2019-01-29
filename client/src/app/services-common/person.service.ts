import { Http, Headers, RequestOptions } from '@angular/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Person } from '../model2/person'
import { PersonInRole } from "../model2/person-in-role";

@Injectable()
export class PersonService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: Http
  ) { }

  getAll(): Observable<Person[]> {
    return this.http.get(env.contextPath + env.apiPath + '/persons')
      .map(response => response.json() as Person[])
  }

  save(person: Person): Observable<Person> {
    const path: string = env.contextPath + env.apiPath + '/persons/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, person, options)
      .map(response => response.json() as Person)
      .do(person => {
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

  delete(personId: string): Observable<any> {
    const path: string = env.contextPath + env.apiPath + '/persons/' + personId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.person.delete.result.success')
      })
  }

  getRoleReferences(person: Person): Observable<PersonInRole[]> {
    return this.http.get(env.contextPath + env.apiPath + '/persons/' + person.id + '/roles/')
      .map(response => response.json() as PersonInRole[])
  }

  search(searchText = ""): Observable<Person[]> {
    return this.http.get(env.contextPath + env.apiPath + '/persons?query=' + searchText + '&max=-1')
      .map(response => response.json() as Person[])
  }


}
