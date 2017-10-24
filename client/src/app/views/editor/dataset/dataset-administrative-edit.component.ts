import {ActivatedRoute, Router} from '@angular/router';
import {
  Component, OnInit, ViewChild,
  AfterContentChecked
} from '@angular/core'
import {NgForm, AbstractControl} from '@angular/forms'
import {Observable} from 'rxjs'
import {Title} from '@angular/platform-browser'
import {TranslateService} from '@ngx-translate/core';

import {Dataset} from '../../../model2/dataset';
import {EditorDatasetService} from '../../../services-editor/editor-dataset.service'
import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {LangPipe} from '../../../utils/lang.pipe'
import {SidebarActiveSection} from './sidebar/sidebar-active-section'

@Component({
    templateUrl: './dataset-administrative-edit.component.html',
    providers: [LangPipe]
})
export class DatasetAdministrativeEditComponent implements OnInit, AfterContentChecked {

    dataset: Dataset;

    @ViewChild('datasetForm') datasetForm: NgForm
    currentForm: NgForm
    formErrors: any = {}

    language: string;

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    sidebarActiveSection = SidebarActiveSection.ADMINISTRATIVE_INFORMATION

    constructor(
        private datasetService: EditorDatasetService,
        private growlMessageService: GrowlMessageService,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService,
        private langPipe: LangPipe,
        private titleService: Title
    ) {
        this.language = this.translateService.currentLang
    }


    ngOnInit() {
        this.getDataset();
    }

    private getDataset() {
        const datasetId = this.route.snapshot.params['datasetId'];
        if (datasetId) {
            Observable.forkJoin(
                this.datasetService.getDataset(datasetId)
            ).subscribe(
                data => {
                    this.dataset = this.datasetService.initializeProperties(data[0])
                    this.updatePageTitle()
                })
        } else {
            this.dataset = this.datasetService.initNew()
        }
    }

    private updatePageTitle():void {
        if(this.dataset.prefLabel) {
            let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
            let bareTitle:string = this.titleService.getTitle();
            this.titleService.setTitle(translatedLabel + " - " + bareTitle)
        }
    }

    ngAfterContentChecked(): void {
      if (this.datasetForm) {
        if (this.datasetForm !== this.currentForm) {
          this.currentForm = this.datasetForm
          this.currentForm.valueChanges.subscribe(data => this.validate(data))
        }
      }
    }

    private validate(data?: any): void {
      this.formErrors = []

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

    save() {
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

        this.datasetService.save(this.dataset)
            .finally(() => {
              this.savingInProgress = false
            })
            .subscribe(savedDataset => {
                this.dataset = savedDataset;
                this.goBack();
            });
    }

    goBack() {
        if (this.dataset.id) {
            this.router.navigate(['/editor/datasets', this.dataset.id, 'administrative-information']);
        } else {
            this.router.navigate(['/editor/datasets']);
        }
    }
}
