import { Component, Input } from '@angular/core'
import { EditorHelpTextComponent } from './editor-help-text.component'

@Component({
  selector: 'editor-help-link',
  template: `
<a *ngIf="helpTextComponent"
   (click)="toggle($event)"
   [ngClass]="{ active: helpTextComponent.visible }"
   href="#"
   class="help-link">
  {{ '?' | translate }}
</a>
`,
  styles: [`
a {
    border-radius: 10px;
    color: #fff;
    padding: 0 6px;
    font-weight: 700;
    font-size: 12px;
    background: #02837a;
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
