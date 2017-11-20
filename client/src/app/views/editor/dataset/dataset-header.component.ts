import { Component, Input } from '@angular/core'

@Component({
  selector: 'dataset-header',
  template: `
<div class="content-header container-fluid">
  <div class="row">
    <div class="col-sm-8">
      <h1>{{ dataset.prefLabel | lang }}</h1>
    </div>
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
