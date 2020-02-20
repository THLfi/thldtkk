import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { TranslateService } from '@ngx-translate/core';
import { NgForm } from '@angular/forms'
import { SupplementaryDigitalSecurityPrinciple } from "../../../model2/supplementary-digital-security-principle"
import { SupplementaryPhysicalSecurityPrinciple } from "../../../model2/supplementary-physical-security-principle"

import { GrowlMessageService } from '../../../services-common/growl-message.service'


type SecurityPrinciple = SupplementaryPhysicalSecurityPrinciple | SupplementaryDigitalSecurityPrinciple

@Component({
  selector: 'security-principle-edit-modal',
  templateUrl: './security-principle-edit-modal.component.html'
})
export class SecurityPrincipleEditModalComponent implements AfterContentChecked {

  @Input() securityPrinciple: SecurityPrinciple

  @ViewChild('securityPrincipleForm') securityPrincipleForm: NgForm
  currentForm: NgForm
  formErrors: { [key: string]: any[] } = { }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<SecurityPrinciple> = new EventEmitter<SecurityPrinciple>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
    this.language = this.translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.securityPrincipleForm) {
      if (this.securityPrincipleForm !== this.currentForm) {
        this.currentForm = this.securityPrincipleForm
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

    this.onSave.emit(this.securityPrinciple)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
