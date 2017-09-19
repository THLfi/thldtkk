import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { environment as env} from '../../environments/environment'

import { Concept } from '../model2/concept'

@Injectable()
export class ConceptService {

  constructor(
    private http: Http
  ) { }

  search(searchText: string): Observable<Concept[]> {
    return this.http.get(env.contextPath + '/api/v3/concepts?query=' + searchText + '&max=50')
      .map(response => response.json() as Concept[])
  }

}
