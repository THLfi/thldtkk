import { Component, Input } from '@angular/core'

@Component({
  selector: 'fa',
  template: '<i class="fa fa-{{ icon }} {{ class }}" aria-hidden="true"></i>'
})
export class FontAwesomeComponent {
  @Input() icon: string
  @Input() class: string = ''
}
