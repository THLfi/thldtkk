import { Component, Input } from '@angular/core'

import { CodeList } from '../../../model2/code-list'

@Component({
  selector: 'viewCodeListCodeItemsModal',
  templateUrl: './view-code-list-code-items-modal.component.html'
})
export class ViewCodeListCodeItemsModalComponent {

  @Input() codeList: CodeList

  visible: boolean = false

  toggle(): void {
    this.visible = !this.visible
  }

}
