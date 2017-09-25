import { ActivatedRoute, Router } from '@angular/router'
import { Component, OnInit } from '@angular/core'

import { CurrentUserService } from '../../services-editor/user.service'

@Component({
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  username: string
  password: string

  returnUrl: string
  showLogoutSuccessMessage: boolean = false
  loginInProgress: boolean = false
  showLoginFailedMessage: boolean = false

  constructor (
    private activatedRoute: ActivatedRoute,
    private currentUserService: CurrentUserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.returnUrl = this.activatedRoute.snapshot.queryParams['returnUrl'] || '/editor'
    this.showLogoutSuccessMessage = this.activatedRoute.snapshot.queryParamMap.get('logout') ? true : false
  }

  login() {
    this.loginInProgress = true
    this.showLogoutSuccessMessage = false

    this.currentUserService.login(this.username, this.password)
      .subscribe(loginSuccessful => {
        if (loginSuccessful) {
          this.router.navigateByUrl(this.returnUrl, { replaceUrl: true })
        }
        else {
          this.showLoginFailedMessage = true
        }

        this.loginInProgress = false
      })
  }

}
