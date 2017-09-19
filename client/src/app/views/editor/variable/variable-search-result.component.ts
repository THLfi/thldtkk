import { Component, Input } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { Variable } from '../../../model2/variable'
import { VariableSearchComponent } from './variable-search.component'
import { VariableService } from '../../../services-common/variable.service'

@Component({
  templateUrl: './variable-search-result.component.html',
  selector: 'variable-search-result',
  styleUrls: ['./variable-search-result.component.css']
})
export class VariableSearchResultComponent {

  @Input() variable: Variable
  @Input() parent: VariableSearchComponent

  editVariable : boolean
  savingInProgress : boolean
  language : string

  constructor(
    private instanceVariableService: EditorInstanceVariableService,
    private translateService: TranslateService,
    private variableService: VariableService
  ) {
        this.language = this.translateService.currentLang
   }

  showVariableModal(variable): void {
    this.editVariable = true
    }

  saveVariable(variable): void {
    this.variableService.save(variable)
      .subscribe(savedVariable => {
        this.closeVariableModal()
    })
  }

  confirmRemove(variableId : string): void {
    this.instanceVariableService.searchInstanceVariableByVariableId(variableId, 100)
      .subscribe((instanceVariables) => {
        var referencingVariables = ""
        instanceVariables.forEach((item) => {
          referencingVariables += (item.prefLabel[this.language]) + "\n"
        })
        this.translateService.get('operations.variable.delete.confirmVariableDelete')
          .subscribe((message: string) => {
            if (referencingVariables.length){
              this.translateService.get('operations.variable.delete.referencingInstanceVariables')
                .subscribe((referenceWarning: string) => {
                  message += "\n\n" + referenceWarning + "\n" + referencingVariables
                })
            }
            if (confirm(message)) {
              this.savingInProgress = true

               this.variableService.delete(variableId)
                .finally(() => {
                  this.savingInProgress = false
                })
                .subscribe(() => {
                  this.parent.refresh()
                }
              );
            }
          })
      })
  }

  closeVariableModal(): void {
    this.editVariable = false
  }

}
