import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot
} from '@angular/router'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'

import { CurrentUserService } from './services-editor/user.service'
import { StringUtils } from './utils/string-utils'

@Injectable()
export class RequireLoginGuard implements CanActivate, CanActivateChild {

  constructor(
    private userService: CurrentUserService,
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return Observable.create((observer) => {
      this.userService.getCurrentUserObservable()
        .subscribe(user => {
          if (user && StringUtils.isNotBlank(user.username)) {
            observer.next(true)
          }
          else {
            this.router.navigate(['/login'])
            observer.next(false)
          }
          observer.complete()
        })
    })
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.canActivate(childRoute, state)
  }

}
