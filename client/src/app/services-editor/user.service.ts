
import {of as observableOf,  BehaviorSubject, Observable } from 'rxjs';

import {catchError, tap, mergeMap, map, skipWhile} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { NavigationExtras, Router } from '@angular/router'

import { environment as env } from '../../environments/environment'

import { LangPipe } from '../utils/lang.pipe'
import { Organization } from '../model2/organization'
import { User } from '../model2/user'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class CurrentUserService {

  private currentUserSubject: BehaviorSubject<User>

  constructor(
    private langPipe: LangPipe,
    private http: HttpClient,
    private router: Router
  ) {
    this.currentUserSubject = new BehaviorSubject(null)
    this.refreshCurrentUser()
  }

  private refreshCurrentUser() {
    this.currentUserSubject.next(null)

    this.http.get<User>(env.contextPath + env.apiPath + '/user-functions/get-current-user')
      .subscribe(user => {
        this.currentUserSubject.next(user)
      })
  }

  getCurrentUserObservable(): Observable<User> {
    return this.currentUserSubject.pipe(skipWhile((user) => user === null))
  }

  isUserAdmin(): Observable<boolean> {
    return this.getCurrentUserObservable().pipe(
      map(user => user ? user.isAdmin : false))
  }

  isUserOrganizationAdmin(): Observable<boolean> {
    return this.getCurrentUserObservable().pipe(
      map(user => user ? user.isOrganizationAdmin : false))
  }

  getUserOrganizations(): Observable<Organization[]> {
    return this.getCurrentUserObservable().pipe(
      mergeMap((user: User) => {
        if (user && user.isLoggedIn) {
          return this.http.post<Organization[]>(env.contextPath + env.apiPath + '/user-functions/list-current-user-organizations', {}).pipe(
            tap(organizations => {
              organizations.forEach(organization => {
                organization.organizationUnit.sort((one, two) => {
                  const onePrefLabel = this.langPipe.transform(one.prefLabel)
                  const twoPrefLabel = this.langPipe.transform(two.prefLabel)
                  return onePrefLabel.localeCompare(twoPrefLabel)
                })
              })
            }))
        }
        else {
          return observableOf([])
        }
      }))
  }

  login(username: string, password: string): Observable<boolean> {
    const formData = new FormData()
    formData.append('username', username)
    formData.append('password', password)

    return this.http.post(env.contextPath + env.apiPath + '/user-functions/login', formData, { observe: 'response' }).pipe(
      map(response => response.status == 204),
      catchError(() => observableOf(false)),
      tap(loginSuccessful => {
        if (loginSuccessful) {
          this.refreshCurrentUser()
        }
      }),)
  }

  logout() {
    this.http.post(env.contextPath + env.apiPath + '/user-functions/logout', {})
      .subscribe(() => {
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
