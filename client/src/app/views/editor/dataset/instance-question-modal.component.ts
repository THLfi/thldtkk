import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { InstanceQuestion } from '../../../model2/instance-question'

@Component({
  selector: 'instance-question-edit-modal',
  templateUrl: './instance-question-modal.component.html'
})
export class InstanceQuestionEditModalComponent implements AfterContentChecked {

  @Input() question: InstanceQuestion

  @ViewChild('instanceQuestionForm') questionForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<InstanceQuestion> = new EventEmitter<InstanceQuestion>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.questionForm) {
      if (this.questionForm !== this.currentForm) {
        this.currentForm = this.questionForm
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

    this.onSave.emit(this.question)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
