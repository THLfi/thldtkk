import { Component, Input } from '@angular/core'

import { Variable } from '../../../model2/variable'
import { VariableService } from '../../../services2/variable.service'

@Component({
  templateUrl: './variable-search-result.component.html',
  selector: 'variable-search-result',
  styleUrls: ['./variable-search-result.component.css']
})
export class VariableSearchResultComponent {

  @Input() variable: Variable

  editVariable : boolean

  constructor(
    private variableService: VariableService
  ) { }

  showVariableModal(variable): void {
    this.editVariable = true
    }

  saveVariable(variable): void {
    this.variableService.saveVariable(variable)
      .subscribe(savedVariable => {
        this.closeVariableModal()
    })
  }

  closeVariableModal(): void {
    this.editVariable = false
  }

}
