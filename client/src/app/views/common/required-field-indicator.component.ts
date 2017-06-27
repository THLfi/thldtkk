import { Component } from '@angular/core'

@Component({
  selector: 'thl-requiredFieldIndicator',
  template: `<i pTooltip="{{ 'requiredField' | translate }}" tooltipPosition="right" class="fa fa-asterisk" aria-hidden="true"></i>
<small class="sr-only">
  {{ 'required' | translate }}
</small>`
})
export class RequiredFieldIndicator { }
