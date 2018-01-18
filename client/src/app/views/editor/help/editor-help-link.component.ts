import { Component, Input } from '@angular/core'
import { EditorHelpTextComponent } from './editor-help-text.component'

@Component({
  selector: 'editor-help-link',
  template: `
<a (click)="toggle($event)"
   [ngClass]="{ active: helpTextComponent.visible }"
   href="#"
   class="help-link">
  {{ 'helpLink' | translate }}
</a>
`,
  styles: [`
a {
  display: inline-block;
  margin-left: .3em;
  border-bottom: 1px dotted #ccc;
  border-radius: 2px;
  color: #01a197;
  font-size: 12px;
  font-weight: 400;
  text-transform: uppercase;
  text-decoration: none;
}

a.active {
  font-weight: 600;
}
`]
})
export class EditorHelpLinkComponent {

  @Input() helpTextComponent: EditorHelpTextComponent

  toggle(event?: Event): void {
    if (event) {
      event.preventDefault()
    }
    this.helpTextComponent.toggle()
  }

}
