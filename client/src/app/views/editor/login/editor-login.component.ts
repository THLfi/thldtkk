import { ActivatedRoute, Router} from '@angular/router'
import { Component, OnInit} from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { Observable } from 'rxjs'
import { CurrentUserService } from '../../../services-editor/user.service'

@Component({
  templateUrl: './editor-login.component.html',
  styleUrls: ['./editor-login.component.css']
})
export class EditorLoginComponent implements OnInit {

  username: string
  password: string

  returnUrl: string
  showLogoutSuccessMessage: boolean = false
  loginInProgress: boolean = false
  showLoginFailedMessage: boolean = false

  thlLoginExpanded: boolean = false;

  logoutMessage: string
  logoutMessageDetails: string

  constructor(
    private activatedRoute: ActivatedRoute,
    private currentUserService: CurrentUserService,
    private router: Router,
    private translateService: TranslateService
) { }

  ngOnInit() {
    this.returnUrl = this.activatedRoute.snapshot.queryParams['returnUrl'] || '/editor'
    let shouldShowLogoutSuccessMessage = this.showLogoutSuccessMessage = this.activatedRoute.snapshot.queryParamMap.get('logout') ? true : false

    if(shouldShowLogoutSuccessMessage) {
      Observable.forkJoin(
        this.translateService.get('editorLoginComponent.logoutSuccess'),
        this.translateService.get('editorLoginComponent.logoutSuccessDetails')
      ).subscribe(data => {
        this.logoutMessage = data[0]
        this.logoutMessageDetails = data[1]
        this.showLogoutSuccessMessage = true
      })
    }

  }

  toggleThlLoginExpanded(): void {
    this.thlLoginExpanded = !this.thlLoginExpanded
  }

  thlLogin(): void {
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

  virtuLogin(): void {
    // TODO: implement login for Virtu
  }

  closeLogoutMessage(): void {
    this.showLogoutSuccessMessage = false
  }

}
