import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { GrowlMessageService } from '../../../services2/growl-message.service'
import { UnitType } from '../../../model2/unit-type'

@Component({
  selector: 'unit-type-edit-modal',
  templateUrl: './unit-type-edit-modal.component.html'
})
export class UnitTypeEditModalComponent implements AfterContentChecked {

  @Input() unitType: UnitType

  @ViewChild('unitTypeForm') unitTypeForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<UnitType> = new EventEmitter<UnitType>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.unitTypeForm) {
      if (this.unitTypeForm !== this.currentForm) {
        this.currentForm = this.unitTypeForm
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
      this.growlMessageService.buildAndShowMessage('error',
        'operations.common.save.result.fail.summary',
        'operations.common.save.result.fail.detail')
      this.savingInProgress = false
      this.savingHasFailed = true
      return
    }

    this.savingInProgress = false

    this.onSave.emit(this.unitType)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
