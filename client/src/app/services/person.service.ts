import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env} from "../../environments/environment";

import { Person } from '../model/person';

@Injectable()
export class PersonService {
  constructor(private _http: Http) {
  }

  getPerson(personId: String): Observable<Person> {
    return this._http.get(env.contextPath + '/api/persons/' + personId )
      .map(response => response.json() as Person);
  }
}
