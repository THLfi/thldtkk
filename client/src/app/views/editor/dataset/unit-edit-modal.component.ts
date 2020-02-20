import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { Unit } from '../../../model2/unit'

@Component({
  selector: 'unit-edit-modal',
  templateUrl: './unit-edit-modal.component.html'
})
export class UnitEditModalComponent implements AfterContentChecked {

  @Input() unit: Unit

  @ViewChild('unitForm') unitForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': [],
    'symbol': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<Unit> = new EventEmitter<Unit>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.unitForm) {
      if (this.unitForm !== this.currentForm) {
        this.currentForm = this.unitForm
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

    this.onSave.emit(this.unit)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
