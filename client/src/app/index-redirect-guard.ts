import {
  ActivatedRouteSnapshot,
  CanActivate,
  RouterStateSnapshot, Router
} from '@angular/router'
import { Injectable } from '@angular/core'

@Injectable()
export class IndexRedirectGuard implements CanActivate {

  constructor(
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (window.location.hostname.match(/.*editor.*/)) {
      this.router.navigate(['editor'])
    }
    else {
      this.router.navigate(['catalog'])
    }
    return false
  }

}
