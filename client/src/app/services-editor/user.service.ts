import { BehaviorSubject, Observable } from 'rxjs'
import { Http } from '@angular/http'
import { Injectable } from '@angular/core'
import { NavigationExtras, Router } from '@angular/router'

import { environment as env } from '../../environments/environment'

import { Organization } from '../model2/organization'
import { User } from '../model2/user'

@Injectable()
export class CurrentUserService {

  private currentUserSubject: BehaviorSubject<User>

  constructor(
    private http: Http,
    private router: Router
  ) {
    this.currentUserSubject = new BehaviorSubject(null)
    this.refreshCurrentUser()
  }

  private refreshCurrentUser() {
    this.currentUserSubject.next(null)

    this.http.get(env.contextPath + '/api/v3/user-functions/get-current-user')
      .map(response => response.json() as User)
      .subscribe(user => {
        this.currentUserSubject.next(user)
      })
  }

  getCurrentUserObservable(): Observable<User> {
    return this.currentUserSubject.skipWhile((user) => user === null)
  }

  isUserAdmin(): Observable<boolean> {
    return this.getCurrentUserObservable()
      .map(user => user ? user.isAdmin : false)
  }

  getUserOrganizations(): Observable<Organization[]> {
    return this.getCurrentUserObservable()
      .flatMap((user: User) => {
        if (user && user.isLoggedIn) {
          return this.http.post(env.contextPath + '/api/v3/user-functions/list-current-user-organizations', {})
            .map(response => response.json() as Organization[])
        }
        else {
          return Observable.of([])
        }
      })
  }

  login(username: string, password: string): Observable<boolean> {
    const formData = new FormData()
    formData.append('username', username)
    formData.append('password', password)

    return this.http.post(env.contextPath + '/api/v3/user-functions/login', formData)
      .map(response => response.json() as boolean)
      .do(loginSuccessful => {
        if (loginSuccessful) {
          this.refreshCurrentUser()
        }
      })
  }

  logout() {
    this.http.post(env.contextPath + '/api/v3/user-functions/logout', {})
      .subscribe(response => {
        this.refreshCurrentUser()

        const extras: NavigationExtras = {
          queryParams: {
            logout: true
          }
        }
        this.router.navigate(['login'], extras)
      })
  }

}
