import { Component, Input } from '@angular/core'

import { Dataset } from '../../../model2/dataset'
import { Study } from '../../../model2/study'

@Component({
  selector: 'dataset-tabs',
  template: `
<div class="row tab-row">
  <div class="col-xs-12">
    <nav>
      <ul class="nav nav-tabs">
        <li routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }" role="presentation">
          <a routerLink="/editor/studies/{{study.id}}/datasets/{{dataset.id}}">
            {{ 'datasetTabs.basicInformation' | translate }}
          </a>
        </li>
        <li routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }" role="presentation">
          <a routerLink="/editor/studies/{{study.id}}/datasets/{{dataset.id}}/instanceVariables">
            {{ 'datasetTabs.instanceVariables' | translate }}
            ({{ dataset.instanceVariables.length }})
          </a>
        </li>
      </ul>
    </nav>
  </div>
</div>
`,
  styleUrls: [ './dataset-tabs.component.css' ]
})
export class DatasetTabsComponent {

  @Input() study: Study
  @Input() dataset: Dataset

}
