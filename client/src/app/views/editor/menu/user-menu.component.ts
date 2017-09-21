import {
  Component, Input, Output, EventEmitter,
  HostListener, ViewChild, ElementRef
} from '@angular/core'

import { StringUtils } from '../../../utils/string-utils'
import { User } from '../../../model2/user'

@Component({
  selector: 'userMenu',
  template: `
<ul *ngIf="currentUser && currentUser.username"
    class="nav navbar-nav navbar-right">
  <li [ngClass]="{ 'dropdown': true, 'open' : dropdownOpen }">
    <a #dropdownToggle
       (click)="toggleDropdownVisibility($event)"
       class="dropdown-toggle"
       role="button"
       aria-haspopup="true"
       [attr.aria-expanded]="dropdownOpen">
      {{ getUserDisplayName() }}
      <i class="fa fa-caret-down" aria-hidden="true"></i>
    </a>
    <ul class="dropdown-menu">
      <li>
        <a (click)="logout()">
          {{ 'mainMenu.userMenu.logoutLink' | translate }}
        </a>
      </li>
    </ul>
  </li>
</ul>`
})
export class UserMenuComponent {

  @Input() currentUser: User
  @Output() onLogout = new EventEmitter<void>()

  @ViewChild("dropdownToggle") dropdownToggle: ElementRef

  dropdownOpen: boolean = false

  getUserDisplayName(): string {
    let displayName: string = ''

    if (StringUtils.isNotBlank(this.currentUser.firstName)) {
      displayName += this.currentUser.firstName
    }

    if (StringUtils.isNotBlank(this.currentUser.lastName)) {
      displayName += this.currentUser.lastName
    }

    if (StringUtils.isBlank(displayName)) {
      displayName = this.currentUser.email
    }

    if (StringUtils.isBlank(displayName)) {
      displayName = this.currentUser.username
    }

    return displayName
  }

  toggleDropdownVisibility(event: any): void {
    event.preventDefault()
    this.dropdownOpen = !this.dropdownOpen
  }

  logout(): void {
    this.dropdownOpen = false
    this.onLogout.emit()
  }

  @HostListener('document:click', ['$event'])
  @HostListener('document:touchstart', ['$event']) // for iOS
  handleOutsideClick(event): void {
    if (this.dropdownOpen && !this.dropdownToggle.nativeElement.contains(event.target)) {
      this.dropdownOpen = false
    }
  }

}
