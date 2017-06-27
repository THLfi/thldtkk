import { Component } from '@angular/core'

@Component({
  selector: 'thl-spinner',
  template: `<i class="fa fa-refresh fa-spin fa-3x fa-fw" aria-hidden="true"></i>
<span class="sr-only">{{ 'loading' | translate }}</span>`
})
export class LoadingSpinner {
}
