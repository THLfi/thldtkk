import { Component, Input } from '@angular/core'

@Component({
  selector: 'thl-spinner-inline',
  template: `<i class="fa fa-refresh fa-spin fa-{{ size }}x fa-fw" aria-hidden="true"></i>
  <span [ngClass]="{ 'sr-only': !showLoadingText }">{{ loadingTextKey | translate }}</span>
`,
  styleUrls: ['./loading-spinner.component.css']
})
export class LoadingSpinnerInline {

  @Input()
  loadingTextKey: string = 'loading'
  @Input()
  showLoadingText: boolean = true
  @Input()
  size: number = 1

}
