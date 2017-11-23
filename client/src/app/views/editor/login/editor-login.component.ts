import { ActivatedRoute, Router} from '@angular/router'
import { Component, OnInit} from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../../../environments/environment'

import { Observable } from 'rxjs'
import { CurrentUserService } from '../../../services-editor/user.service'
import { Http } from '@angular/http'
import { StringUtils } from '../../../utils/string-utils'

@Component({
  templateUrl: './editor-login.component.html',
  styleUrls: ['./editor-login.component.css']
})
export class EditorLoginComponent implements OnInit {

  readonly THL_LOGIN_EXPANDED_LOCAL_STORAGE_KEY: string = 'EditorLoginComponent.thlLoginExpanded'

  returnUrl: string

  username: string
  password: string

  showVirtuLoginButton: boolean = false
  virtuIdpDirectoryServiceUrl: string
  showVirtuLoginFailedMessage: boolean = false

  thlLoginExpanded: boolean = false
  thlLoginInProgress: boolean = false
  showThlLoginFailedMessage: boolean = false

  showLogoutSuccessMessage: boolean = false
  logoutMessage: string
  logoutMessageDetails: string

  constructor(
    private activatedRoute: ActivatedRoute,
    private currentUserService: CurrentUserService,
    private router: Router,
    private translateService: TranslateService,
    private http: Http
  ) { }

  ngOnInit() {
    this.http.get(env.contextPath + '/api/v3/virtu/idpDirectoryServiceUrl')
      .map(response => response.json() as string)
      .subscribe(url => {
        if (StringUtils.isNotBlank(url)) {
          this.virtuIdpDirectoryServiceUrl = url
          this.showVirtuLoginButton = true
        }
      })

    if (window.localStorage) {
      this.thlLoginExpanded = StringUtils.isNotBlank(window.localStorage.getItem(this.THL_LOGIN_EXPANDED_LOCAL_STORAGE_KEY))
    }

    this.returnUrl = this.activatedRoute.snapshot.queryParams['returnUrl'] || '/editor'

    this.showVirtuLoginFailedMessage = this.activatedRoute.snapshot.queryParamMap.get('virtuLoginError') ? true : false

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

    if (window.localStorage) {
      if (this.thlLoginExpanded) {
        window.localStorage.setItem(this.THL_LOGIN_EXPANDED_LOCAL_STORAGE_KEY, 'true')
      }
      else {
        window.localStorage.removeItem(this.THL_LOGIN_EXPANDED_LOCAL_STORAGE_KEY)
      }
    }
  }

  thlLogin(): void {
    this.thlLoginInProgress = true
    this.showThlLoginFailedMessage = false
    this.showLogoutSuccessMessage = false

    this.currentUserService.login(this.username, this.password)
      .subscribe(loginSuccessful => {
        if (loginSuccessful) {
          this.router.navigateByUrl(this.returnUrl, { replaceUrl: true })
        }
        else {
          this.showThlLoginFailedMessage = true
        }

        this.thlLoginInProgress = false
      })
  }

  redirectToVirtuLogin(): void {
    window.location.href = this.virtuIdpDirectoryServiceUrl
  }

  closeLogoutMessage(): void {
    this.showLogoutSuccessMessage = false
  }

}
