import { Component, Input } from '@angular/core'

@Component({
  selector: 'glyphicon',
  template: '<span class="glyphicon glyphicon-{{ icon }}" aria-hidden="true"></span>'
})
export class GlyphiconComponent {
  @Input() icon: string
}
