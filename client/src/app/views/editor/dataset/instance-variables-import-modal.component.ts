import {
  Component, Input, EventEmitter, Output, OnInit,
  ViewChild, AfterContentChecked
} from '@angular/core'
import { NgForm } from '@angular/forms'
import { forkJoin } from 'rxjs'
import { PapaParseConfig, Papa } from 'ngx-papaparse'
import { TranslateService } from '@ngx-translate/core'
import Swal from 'sweetalert2';

import { Dataset } from '../../../model2/dataset'
import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { Study } from '../../../model2/study'
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'instance-variables-import-modal',
  styleUrls: [ './instance-variables-import-modal.component.css' ],
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
  overwrite: boolean = false
  encoding: string
  showPreview: boolean = false
  csvJSON: Object

  importInProgress: boolean = false
  importHasFailed: boolean = false

  parseOptions: PapaParseConfig = {
      header: true,
      preview: 25,
      skipEmptyLines: true
  }

  @Output() onImport: EventEmitter<void> = new EventEmitter<void>()
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private instanceVariableService: EditorInstanceVariableService,
    private papaParseService: Papa,
    private translateService: TranslateService
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
    this.showPreview = false

    this.validate()
  }

  doImport(): void {
    this.importInProgress = true
    this.showPreview = false

    this.validate()

    if (this.currentForm.invalid || this.formErrors.file.length) {
      this.importInProgress = false
      this.importHasFailed = true
      return
    }

    this.instanceVariableService.importInstanceVariablesAsCsv(this.study.id, this.dataset.id, this.file, this.encoding, this.overwrite)
      .pipe(
        finalize(() => {
          this.importInProgress = false
        }))
      .subscribe(result => {
        this.importInProgress = false
        this.importHasFailed = false

        // collect all row messages
        const messages: Array<String> = result.messages.slice(0)
        for (let i = 0; i < result.parsedObject.length; i++) {
          for (let j = 0; j < result.parsedObject[i].messages.length; j++) {
            messages.push(result.parsedObject[i].messages[j] + '|' + (i + 1))
          }
        }

        if (!Array.isArray(messages) || !messages.length) {
          this.showSwalSuccess(result.parsedObject.length)
        } else {
          this.showSwalErrors(messages)
        }

        this.file = null
        this.onImport.emit()
      }, (errorResponse: HttpErrorResponse) => {
        const messages: Array<String> = errorResponse.error.messages
        if (Array.isArray(messages) && messages.length > 0) {
          this.showSwalErrors(messages)
        }
      })
  }

  doCancel(): void {
    this.showPreview = false
    this.file = null
    this.onCancel.emit()
  }

  showWarning(): void {
    this.translateService.get('importInstanceVariablesModal.overwrite.warningQuestion')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.doImport()
      }})
  }

  importButtonClick(): void {
    if (this.overwrite) {
      this.showWarning()
    } else {
      this.doImport()
    }
  }

  doPreview(): void {
    this.showPreview = !this.showPreview

    if (this.showPreview) {
      const reader: FileReader = new FileReader()
      reader.readAsText(this.file, this.encoding)

      reader.onload = () => {
        const csvString = (reader.result as string).replace(/^.*/, function(m) {
            return m.replace(/\./g, '_')
        })

        this.csvJSON = this.papaParseService.parse(csvString, this.parseOptions).data
      }
    }
  }

  private showSwalSuccess(instanceVariablesImported: number): void {
    forkJoin(
      this.translateService.get('importInstanceVariablesModal.result.success'),
      this.translateService.get('importInstanceVariablesModal.result.instanceVariablesImported')
    ).subscribe(data => {
      Swal.fire({
        title: data[0] as string,
        html: instanceVariablesImported + ' ' + (data[1] as string),
        type: 'success',
        animation: false
      })
    })
  }

  private showSwalErrors(messages: Array<String>): void {
    const messagesToTranslate: string[] = []
    messages.forEach( (message) => {
      const stringToTranslate: string = message as string
      messagesToTranslate.push(stringToTranslate.split('|', 1)[0])
    })

    const observableBatch = []
    observableBatch.push(this.translateService.get('importInstanceVariablesModal.result.errors'))
    observableBatch.push(this.translateService.get('importInstanceVariablesModal.result.row'))
    observableBatch.push(this.translateService.get('importInstanceVariablesModal.result.column'))
    messagesToTranslate.forEach( (message) => {
      observableBatch.push(this.translateService.get(message))
    })

    forkJoin(
      observableBatch
    ).subscribe(data => {
      const translatedMessages: string[] = []
      for (let i = 3; i < data.length; i++) {
        const message: string = messages[i - 3] as string
        if (message.indexOf('|') > -1) {
          translatedMessages.push((data[1] as string) + ' ' + message.split('|', 3)[2] + ', '
              + (data[2] as string) + ' \"' + message.split('|', 3)[1] + '\": '
              + (data[i] as string))
        } else {
          translatedMessages.push(data[i] as string)
        }
      }

      Swal.fire({
        title: data[0] as string,
        html: translatedMessages.join('<br><br>'),
        type: 'warning',
        animation: false,
        width: '800px'
      })
    })
  }

  downloadExampleInstanceVariableCsv(): string {
    return this.instanceVariableService.downloadExampleInstanceVariableCsv()
  }
}
