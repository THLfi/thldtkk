import { Component, OnInit, Input } from '@angular/core'

import {
  BreadcrumbService,
  Breadcrumb
} from '../../../services-common/breadcrumb.service'
import { User } from '../../../model2/user'

@Component({
  selector: 'breadcrumb',
  template: `
<nav [ngClass]="{
  catalog: breadcrumbStyle === 'catalog',
  editor: breadcrumbStyle === 'editor',
  container: breadcrumbStyle === 'catalog',
  hidden: (breadcrumbStyle === 'catalog' && !(currentBreadcrumbs && currentBreadcrumbs.length > 0)) || !(currentUser && currentUser.isLoggedIn) }"
     class="breadcrumb">
  <ng-container *ngFor="let breadcrumb of currentBreadcrumbs">
    <ng-container *ngIf="breadcrumb.url; else currentView;">
      <a routerLink="{{ breadcrumb.url }}">{{ breadcrumb.label }}</a>
      <span class="divider"><i class="fa fa-angle-right" aria-hidden="true"></i></span>
    </ng-container>
    <ng-template #currentView>
      <span *ngIf="breadcrumbStyle === 'editor'">{{ breadcrumb.label }}</span>
    </ng-template>
  </ng-container>
</nav>
`,
  styleUrls: [ './breadcrumb.component.css' ]
})
export class BreadcrumbComponent implements OnInit {

  currentBreadcrumbs: Breadcrumb[]

  @Input() breadcrumbStyle: 'catalog' | 'editor' = 'catalog'
  @Input() currentUser: User

  constructor(
    private breadcrumbService: BreadcrumbService
  ) { }

  ngOnInit(): void {
    this.breadcrumbService.currentBreadcrumbs.subscribe(newBreadcrumbs => {
      // Timeout is a quickfix to 'Expression has changed after it was checked' exception
      // See https://github.com/angular/angular/issues/6005
      setTimeout(() => this.currentBreadcrumbs = newBreadcrumbs)
    })
  }

}
