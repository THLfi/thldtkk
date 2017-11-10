import { Component, Input } from '@angular/core'

@Component({
  selector: 'dataset-header',
  template: `
<div class="content-header container-fluid">
  <div class="row">
    <div class="col-sm-8">
      <h1>{{ dataset.prefLabel | lang }}</h1>
    </div>
<!-- TODO: Fix publishing
    <div class="col-sm-4">
      <h2 class="pull-right">
        <div class="pull-right">
          <ng-container *ngIf="dataset.published; else unpublished;">
            <span class="label label-success">
              {{ 'published' | translate }}
              <span class="glyphicon glyphicon-ok"></span>
            </span>
            <button (click)="confirmWithdraw()" class="btn btn-sm btn-warning btn-unpublish">
              {{ 'unpublish' | translate }}
            </button>
          </ng-container>
          <ng-template #unpublished>
            <span class="label label-default">
              {{ 'unpublished' | translate }}
            </span>
            <button (click)="confirmPublish()" class="btn btn-sm btn-warning btn-publish">
              {{ 'publish' | translate }}
            </button>
          </ng-template>
        </div>
      </h2>
    </div>
-->
  </div>
  <div class="row">
    <div class="col-xs-12">
      <last-modified [modifier]="dataset.lastModifiedByUser"
                     [lastModifiedDate]="dataset.lastModifiedDate">
      </last-modified>
    </div>
  </div>
</div>
`
})
export class DatasetHeaderComponent {

  @Input() dataset

}
