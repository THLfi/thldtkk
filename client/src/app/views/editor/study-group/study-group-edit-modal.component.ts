import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { AbstractControl, NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { DateUtils } from '../../../utils/date-utils'
import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { StudyGroup } from '../../../model2/study-group'

@Component({
  selector: 'study-group-edit-modal',
  templateUrl: './study-group-edit-modal.component.html'
})
export class StudyGroupEditModalComponent implements AfterContentChecked {

  @Input() studyGroup: StudyGroup

  @ViewChild('studyGroupForm') studyGroupForm: NgForm
  currentForm: NgForm
  formErrors: { [key: string]: any[] } = { }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<StudyGroup> = new EventEmitter<StudyGroup>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private dateUtils: DateUtils,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.studyGroupForm) {
      if (this.studyGroupForm !== this.currentForm) {
        this.currentForm = this.studyGroupForm
        this.currentForm.valueChanges.subscribe(data => this.validate(data))
      }
    }
  }

  private validate(data?: any): void {
    this.formErrors = { }

    for (const name in this.currentForm.form.controls) {
      const control: AbstractControl = this.currentForm.form.get(name)
      if (control && control.invalid && (this.savingInProgress || this.savingHasFailed)) {
        for (const errorKey in control.errors) {
          if (!this.formErrors[name]) {
            this.formErrors[name] = []
          }
          this.formErrors[name] = [
            ...this.formErrors[name],
            'errors.form.' + errorKey
          ]
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

    this.onSave.emit(this.studyGroup)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
