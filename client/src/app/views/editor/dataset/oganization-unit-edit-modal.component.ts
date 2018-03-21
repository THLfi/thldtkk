import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { OrganizationUnit } from '../../../model2/organization-unit'
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'organization-unit-edit-modal',
  templateUrl: './organization-unit-edit-modal.component.html'
})
export class OrganizationUnitEditModalComponent implements AfterContentChecked {

  @Input() organizationUnit: OrganizationUnit

  @ViewChild('organizationUnitForm') organizationUnitForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<OrganizationUnit> = new EventEmitter<OrganizationUnit>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
      this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.organizationUnitForm) {
      if (this.organizationUnitForm !== this.currentForm) {
        this.currentForm = this.organizationUnitForm
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

    this.onSave.emit(this.organizationUnit)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
