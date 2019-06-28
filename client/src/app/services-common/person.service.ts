
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { Person } from '../model2/person'
import { PersonInRole } from "../model2/person-in-role";
import { HttpClient } from '@angular/common/http';

@Injectable()
export class PersonService {

  constructor(
    private growlMessageService: GrowlMessageService,
    private http: HttpClient
  ) { }

  getAll(): Observable<Person[]> {
    return this.http.get<Person[]>(env.contextPath + env.apiPath + '/persons');
  }

  save(person: Person): Observable<Person> {
    const path: string = env.contextPath + env.apiPath + '/persons/'

    return this.http.post<Person>(path, person).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.person.save.result.success')
      }))
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

    return this.http.delete(path).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('info', 'operations.person.delete.result.success')
      }))
  }

  getRoleReferences(person: Person): Observable<PersonInRole[]> {
    return this.http.get<PersonInRole[]>(env.contextPath + env.apiPath + '/persons/' + person.id + '/roles/');
  }

  search(searchText = ""): Observable<Person[]> {
    return this.http.get<Person[]>(env.contextPath + env.apiPath + '/persons?query=' + searchText + '&max=-1');
  }
}
