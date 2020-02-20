import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { TranslateService } from '@ngx-translate/core';
import { NgForm } from '@angular/forms'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { System } from '../../../model2/system'

@Component({
  selector: 'system-edit-modal',
  templateUrl: './system-edit-modal.component.html'
})
export class SystemEditModalComponent implements AfterContentChecked {

  @Input() system: System

  @ViewChild('systemForm') systemForm: NgForm
  currentForm: NgForm
  formErrors: { [key: string]: any[] } = { }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<System> = new EventEmitter<System>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
      this.language = this.translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.systemForm) {
      if (this.systemForm !== this.currentForm) {
        this.currentForm = this.systemForm
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

    this.onSave.emit(this.system)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
