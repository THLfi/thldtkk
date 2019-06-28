import {
  Component, OnInit, Input, Output, EventEmitter, ViewChild,
  AfterContentChecked
} from '@angular/core'
import {NgForm} from '@angular/forms'
import {TranslateService} from '@ngx-translate/core'

import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {Variable} from '../../../model2/variable'
import {VariableService} from '../../../services-common/variable.service'
import { finalize } from 'rxjs/operators';

@Component({
    templateUrl: './variable-edit-modal.component.html',
    selector: 'edit-variable-modal',
})
export class VariableEditModalComponent implements OnInit, AfterContentChecked {

    variable: Variable
    language: string

    @ViewChild('variableForm') variableForm: NgForm
    currentForm: NgForm
    formErrors: any = {
      'prefLabel': []
    }

    @Output() variableSaved:EventEmitter<Variable>

    showDialog: boolean = false

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    constructor(
        private variableService: VariableService,
        private growlMessageService: GrowlMessageService,
        private translateService: TranslateService
    ) {
        this.language = translateService.currentLang
        this.variableSaved = new EventEmitter<Variable>()
    }

    ngOnInit(): void {
      const variable = this.createEmptyVariable()
      this.initVariable(variable)
      this.variable = variable
    }

    private initVariable(variable: Variable): void {
      this.initProperties(variable, ['prefLabel', 'description'])
    }

    private initProperties(variable: Variable, properties: string[]): void {
        properties.forEach(property => {
            if (!variable[property]) {
                variable[property] = {}
            }
            if (!variable[property][this.language]) {
                variable[property][this.language] = ''
            }
        })
    }

    private createEmptyVariable(): Variable {
        const variable = {
                id: null,
                prefLabel: null,
                description: null,
            }
        return variable;
    }

    ngAfterContentChecked(): void {
      if (this.variableForm) {
        if (this.variableForm !== this.currentForm) {
          this.currentForm = this.variableForm
          this.currentForm.valueChanges.subscribe(data => this.validate(data))
        }
      }
    }

    private validate(data?: any): void {
      for (const field in this.formErrors) {
        this.formErrors[field] = []
        const control = this.currentForm.form.get(field)
        if (control && control.invalid && (this.savingInProgress || this.savingHasFailed)) {
          for (const key in control.errors) {
            this.formErrors[field] = [ ...this.formErrors[field], 'errors.form.' + key ]
          }
        }
      }
    }

    clear(): void {
        const variable = this.createEmptyVariable()
        this.initVariable(variable)
        this.variable = variable
    }

    save(): void {
        this.savingInProgress = true

        this.validate()

        if (this.currentForm.invalid) {
          this.growlMessageService.buildAndShowMessage('error',
            'operations.common.save.result.fail.summary',
            'operations.common.save.result.fail.detail')
          this.savingInProgress = false
          this.savingHasFailed = true
          return
        }

        this.variableService.save(this.variable)
            .pipe(finalize(() => {
              this.savingInProgress = false
            }))
            .subscribe(variable => {
                this.variable = variable
                this.variableSaved.emit(variable)
                this.hide()
            })
    }

    show(): void {
        this.showDialog = true;
    }

    hide(): void {
        this.showDialog = false
    }

}
