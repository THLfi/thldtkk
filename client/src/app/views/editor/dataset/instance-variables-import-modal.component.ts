import {
  Component, Input, EventEmitter, Output, OnInit,
  ViewChild, AfterContentChecked
} from '@angular/core'
import { NgForm } from '@angular/forms'

import { Dataset } from '../../../model2/dataset'
import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { Study } from '../../../model2/study'

@Component({
  selector: 'instance-variables-import-modal',
  templateUrl: './instance-variables-import-modal.component.html'
})
export class InstanceVariablesImportModalComponent implements OnInit, AfterContentChecked {

  @Input() study: Study
  @Input() dataset: Dataset

  @ViewChild('importForm') importForm: NgForm
  currentForm: NgForm
  formErrors: any = {
    'file': [],
    'encoding': []
  }

  file: File
  encoding: string

  importInProgress: boolean = false
  importHasFailed: boolean = false

  @Output() onImport: EventEmitter<void> = new EventEmitter<void>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private instanceVariableService: EditorInstanceVariableService
  ) { }

  ngOnInit(): void {
    if (navigator && navigator.platform && navigator.platform.indexOf('Mac') > -1) {
      this.encoding = 'MacRoman'
    }
    else {
      this.encoding = "ISO-8859-15"
    }
  }

  ngAfterContentChecked(): void {
    if (this.importForm) {
      if (this.importForm !== this.currentForm) {
        this.currentForm = this.importForm
        this.currentForm.valueChanges.subscribe(data => this.validate(data))
      }
    }
  }

  private validate(data?: any): void {
    if (this.importInProgress || this.importHasFailed) {
      for (const field in this.formErrors) {
        this.formErrors[field] = []
        const control = this.currentForm.form.get(field)
        if (control && control.invalid) {
          for (const key in control.errors) {
            this.formErrors[field] = [ ...this.formErrors[field], 'errors.form.' + key ]
          }
        }
      }

      if (!this.file) {
        this.formErrors.file = [ 'errors.form.required' ]
      }
    }
  }

  doUpdateFile(event: any): void {
    const files: FileList = event.target.files

    if (files && files.length > 0) {
      this.file = files[0]
    }
    else {
      this.file = null
    }

    this.validate()
  }

  doImport(): void {
    this.importInProgress = true

    this.validate()

    if (this.currentForm.invalid || this.formErrors.file.length) {
      this.importInProgress = false
      this.importHasFailed = true
      return
    }

    this.instanceVariableService.importInstanceVariablesAsCsv(this.study.id, this.dataset.id, this.file, this.encoding)
      .finally(() => {
        this.importInProgress = false
      })
      .subscribe(result => {
        this.importInProgress = false
        this.importHasFailed = false
        this.onImport.emit()
      })
  }

  doCancel(): void {
    this.onCancel.emit()
  }

}
