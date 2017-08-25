import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'

import { GrowlMessageService } from '../../../services2/growl-message.service'
import { Person } from '../../../model2/person'

@Component({
  selector: 'person-edit-modal',
  templateUrl: './person-edit-modal.component.html'
})
export class PersonEditModalComponent implements AfterContentChecked {

  @Input() person: Person

  @ViewChild('personForm') personForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'firstName': []
  }
  // TODO: Add email validation?

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<Person> = new EventEmitter<Person>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService
  ) { }

  ngAfterContentChecked(): void {
    if (this.personForm) {
      if (this.personForm !== this.currentForm) {
        this.currentForm = this.personForm
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

    this.onSave.emit(this.person)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
