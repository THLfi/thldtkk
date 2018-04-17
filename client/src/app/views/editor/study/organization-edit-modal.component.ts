import { AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core'
import { NgForm } from '@angular/forms'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { Organization } from '../../../model2/organization'
import { TranslateService } from "@ngx-translate/core";
import { ConfirmationService } from "primeng/primeng";

@Component({
  selector: 'organization-edit-modal',
  templateUrl: './organization-edit-modal.component.html'
})
export class OrganizationEditModalComponent implements AfterContentChecked {

  @Input() organization: Organization

  @ViewChild('organizationForm') organizationForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<Organization> = new EventEmitter<Organization>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService,
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.organizationForm) {
      if (this.organizationForm !== this.currentForm) {
        this.currentForm = this.organizationForm
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

    this.onSave.emit(this.organization)
  }

  doCancel() {
    this.onCancel.emit()
  }
}
