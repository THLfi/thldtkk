import { Component, OnInit } from '@angular/core'

import {
  BreadcrumbService,
  Breadcrumb
} from '../../../services-common/breadcrumb.service'

@Component({
  selector: 'breadcrumb',
  template: `
<nav class="breadcrumb">
  <ng-container *ngFor="let breadcrumb of currentBreadcrumbs">
    <ng-container *ngIf="breadcrumb.url; else currentView;">
      <a routerLink="{{ breadcrumb.url }}">{{ breadcrumb.label }}</a>
      <span class="divider"><i class="fa fa-angle-right" aria-hidden="true"></i></span>
    </ng-container>
    <ng-template #currentView>
      <span>{{ breadcrumb.label }}</span>
    </ng-template>
  </ng-container>
</nav>
`,
  styleUrls: [ './breadcrumb.component.css' ]
})
export class BreadcrumbComponent implements OnInit {

  currentBreadcrumbs: Breadcrumb[]

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
