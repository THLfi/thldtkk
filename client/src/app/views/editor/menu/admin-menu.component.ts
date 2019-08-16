import {
  Component, Input, Output, EventEmitter,
  HostListener, ViewChild, ElementRef
} from '@angular/core'

import { User } from '../../../model2/user'

@Component({
  selector: 'adminMenu',
  template: `
<ul *ngIf="currentUser && (currentUser.isAdmin||currentUser.isOrganizationAdmin)"
    class="nav navbar-nav">
  <li [routerLinkActive]="['active']" [ngClass]="{ 'dropdown': true, 'open' : dropdownOpen }">
    <a #dropdownToggle
       (click)="toggleDropdownVisibility($event)"
       id="admin-menu-toggle"
       class="dropdown-toggle"
       role="button"
       aria-haspopup="true"
       [attr.aria-expanded]="dropdownOpen">
       <span class="glyphicon glyphicon-cog"></span>
       {{ 'adminMenu.title' | translate }}
      <i class="fa fa-caret-down" aria-hidden="true"></i>
    </a>
    <ul class="dropdown-menu">
      <li id="admin-menu-variables-link" [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/variables/">{{ 'variables' | translate }}</a></li>
      <li id="admin-menu-unit-types-link" [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/unitTypes/">{{ 'unitTypes' | translate }}</a></li>
      <li id="admin-menu-universes-link" [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/universes/">{{ 'universes' | translate }}</a></li>
      <li id="admin-menu-codelists-link" [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/codelists/">{{ 'codeLists' | translate }}</a></li>
      <li id="admin-menu-organizations-link" [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/organizations/">{{ 'organizationsAndOrganizationUnits' | translate }}</a></li>
      <li id="admin-menu-people-link" *ngIf="currentUser.isAdmin" [routerLinkActive]="['active']" [routerLinkActiveOptions]="{ exact: true }"><a routerLink="/editor/persons/">{{ 'people' | translate }}</a></li>
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
