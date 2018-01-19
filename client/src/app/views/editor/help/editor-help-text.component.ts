import { Component, Input, OnInit } from '@angular/core'
import { StringUtils } from '../../../utils/string-utils'

@Component({
  selector: 'editor-help-text',
  template: `
<div *ngIf="visible"
     class="help-text-container">
  <div class="pull-right">
    <a (click)="toggle($event)"
       class="close-link"
       href="#">
      <i class="fa fa-fw fa-close" aria-hidden="true"></i>
      <span class="sr-only">{{ 'close' | translate }}</span>
    </a>
  </div>
  <div [innerHTML]="key | translate"
       class="help-text">
  </div>
</div>
`,
  styles: [`
.help-text-container {
  margin-top: .5em;
  margin-bottom: .5em;
  padding: 1em;
  border-radius: 1px;
  background-color: rgb(192, 221, 219);
}

a.close-link {
  color: rgb(51, 51, 51);
}

.help-text {
  margin-right: 1.5em;
}
`]
})
export class EditorHelpTextComponent implements OnInit {

  @Input() key: string
  @Input() visible: boolean = false

  ngOnInit(): void {
    if (StringUtils.isBlank(this.key)) {
      this.visible = false
      throw new Error('Property \'key\' is required')
    }
  }

  toggle(event?: any): void {
    if (event) {
      event.preventDefault()
    }
    this.visible = !this.visible
  }

}
