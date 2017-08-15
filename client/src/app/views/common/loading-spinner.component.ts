import { Component } from '@angular/core'

@Component({
  selector: 'thl-spinner',
  template: `<div class="thl-spinner-container">
  <i class="fa fa-refresh fa-spin fa-3x fa-fw" aria-hidden="true"></i>
  <span class="sr-only">{{ 'loading' | translate }}</span>
</div>`,
  styleUrls: ['./loading-spinner.component.css']
})
export class LoadingSpinner {
}
