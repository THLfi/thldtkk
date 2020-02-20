import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { Universe } from '../../../model2/universe'

@Component({
  selector: 'universe-edit-modal',
  templateUrl: './universe-edit-modal.component.html'
})
export class UniverseEditModalComponent implements AfterContentChecked {

  @Input() universe: Universe

  @ViewChild('universeForm') universeForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<Universe> = new EventEmitter<Universe>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.universeForm) {
      if (this.universeForm !== this.currentForm) {
        this.currentForm = this.universeForm
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

    this.onSave.emit(this.universe)
  }

  doCancel() {
    this.onCancel.emit()
  }

}
