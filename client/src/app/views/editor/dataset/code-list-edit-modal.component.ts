import {
  AfterContentChecked, Component, EventEmitter, Input, Output, ViewChild
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { TranslateService } from '@ngx-translate/core'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { CodeList } from '../../../model2/code-list'
import { CodeItem } from "../../../model2/code-item";
import { CodeListService3 } from '../../../services-common/code-list.service'
import { StringUtils } from "../../../utils/string-utils";

@Component({
  selector: 'code-list-edit-modal',
  templateUrl: './code-list-edit-modal.component.html'
})
export class CodeListEditModalComponent implements AfterContentChecked {

  @Input() codeList: CodeList

  @ViewChild('codeListForm') codeListForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'prefLabel': []
  }

  language: string

  savingInProgress: boolean = false
  savingHasFailed: boolean = false

  @Output() onSave: EventEmitter<CodeList> = new EventEmitter<CodeList>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService,
    private codeListService: CodeListService3
  ) {
    this.language = translateService.currentLang
  }

  ngAfterContentChecked(): void {
    if (this.codeListForm) {
      if (this.codeListForm !== this.currentForm) {
        this.currentForm = this.codeListForm
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

  addCodeItem(): void {
    const newCodeItem = this.codeListService.initNewCodeItem()
    this.codeList.codeItems = [ ...this.codeList.codeItems, newCodeItem ]
  }

  removeCodeItem(codeItem: CodeItem): void {
    const index: number = this.codeList.codeItems.indexOf(codeItem)
    if (index !== -1) {
      this.codeList.codeItems.splice(index, 1)
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

    this.removeInvalidCodeItemsFromNewCodeList()

    this.savingInProgress = false

    this.onSave.emit(this.codeList)
  }

  private removeInvalidCodeItemsFromNewCodeList() {
    let validCodeItems: CodeItem[] = []
    this.codeList.codeItems.forEach(codeItem => {
      if (StringUtils.isNotBlank(codeItem.code)
        && StringUtils.isNotBlank(codeItem.prefLabel[this.language])) {
        validCodeItems = [ ...validCodeItems, codeItem ]
      }
    })
    this.codeList.codeItems = validCodeItems
  }

  doCancel() {
    this.onCancel.emit()
  }

}
