import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { Variable } from '../../../model2/variable'

@Component({
  selector: 'variable-modal',
  templateUrl: './variable-modal.component.html'
})
export class VariableModalComponent implements AfterContentChecked {

  @Input() variable: Variable

  @ViewChild('variableForm') variableForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': [],
    'description': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<Variable> = new EventEmitter<Variable>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
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

  doSave() {
    this.savingInProgress = true

    this.validate()

    if (this.currentForm.invalid) {
      this.growlMessageService.showCommonSaveFailedMessage()
      this.savingInProgress = false
      this.savingHasFailed = true
      return
    }

    this.savingInProgress = false

    this.onSave.emit(this.variable)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
