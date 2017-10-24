import {
  Component, Input, Output, EventEmitter,
  HostListener, ViewChild, ElementRef
} from '@angular/core'

import { StringUtils } from '../../../utils/string-utils'
import { User } from '../../../model2/user'

@Component({
  selector: 'adminMenu',
  template: `
<ul *ngIf="currentUser && currentUser.isAdmin"
    class="nav navbar-nav">
  <li [ngClass]="{ 'dropdown': true, 'open' : dropdownOpen }">
    <a #dropdownToggle
       (click)="toggleDropdownVisibility($event)"
       class="dropdown-toggle"
       role="button"
       aria-haspopup="true"
       [attr.aria-expanded]="dropdownOpen">
       <span class="glyphicon glyphicon-cog"></span>
       {{ 'adminMenu.title' | translate }}
      <i class="fa fa-caret-down" aria-hidden="true"></i>
    </a>
    <ul class="dropdown-menu">
      <li [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/variables/">{{ 'variables' | translate }}</a></li>
      <li [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/unitTypes/">{{ 'unitTypes' | translate }}</a></li>
      <li [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/universes/">{{ 'universes' | translate }}</a></li>
      <li [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/codelists/">{{ 'codeLists' | translate }}</a></li>
    </ul>
  </li>
</ul>`
})
export class AdminMenuComponent {

  @Input() currentUser: User
  @Output() onLogout = new EventEmitter<void>()

  @ViewChild("dropdownToggle") dropdownToggle: ElementRef

  dropdownOpen: boolean = false

  toggleDropdownVisibility(event: any): void {
    event.preventDefault()
    this.dropdownOpen = !this.dropdownOpen
  }

  @HostListener('document:click', ['$event'])
  @HostListener('document:touchstart', ['$event']) // for iOS
  handleOutsideClick(event): void {
    if (this.dropdownOpen && !this.dropdownToggle.nativeElement.contains(event.target)) {
      this.dropdownOpen = false
    }
  }

}
