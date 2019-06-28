import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { Concept } from '../model2/concept'

@Injectable()
export class ConceptService {

  constructor(
    private http: HttpClient
  ) { }

  search(searchText: string): Observable<Concept[]> {
    return this.http.get<Concept[]>(env.contextPath + env.apiPath + '/concepts?query=' + searchText + '&max=50');
  }

}
