import {ActivatedRoute, Router} from '@angular/router';
import {
  Component, OnInit, ViewChild,
  AfterContentChecked
} from '@angular/core'
import {NgForm, AbstractControl} from '@angular/forms'
import {Title} from '@angular/platform-browser'
import {TranslateService} from '@ngx-translate/core';
import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { DateUtils } from '../../../utils/date-utils'
import {ConfidentialityClass} from '../../../model2/confidentiality-class'
import {EditorStudyService} from '../../../services-editor/editor-study.service'
import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {LangPipe} from '../../../utils/lang.pipe'
import {Study} from '../../../model2/study';
import {StudySidebarActiveSection} from './sidebar/study-sidebar-active-section'

@Component({
    templateUrl: './study-administrative-edit.component.html',
    providers: [LangPipe]
})
export class StudyAdministrativeEditComponent implements OnInit, AfterContentChecked {

    study: Study;

    yearRangeForDataProcessingFields: string =  ('1900:' + (new Date().getFullYear() + 20))
    dataProcessingStartDate: Date
    dataProcessingEndDate: Date

    @ViewChild('studyForm') studyForm: NgForm
    currentForm: NgForm
    formErrors: any = {}
    
    language: string;

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    sidebarActiveSection = StudySidebarActiveSection.ADMINISTRATIVE_INFORMATION

    confidentialityClassType = ConfidentialityClass

    constructor(
        private studyService: EditorStudyService,
        private growlMessageService: GrowlMessageService,
        private route: ActivatedRoute,
        private router: Router,
        private breadcrumbService: BreadcrumbService,
        private translateService: TranslateService,
        private langPipe: LangPipe,
        private titleService: Title,
        private dateUtils: DateUtils
    ) {
        this.language = this.translateService.currentLang
    }


    ngOnInit() {
        this.getStudy();
    }

    private getStudy() {
      const studyId = this.route.snapshot.params['studyId']
      if (studyId) {
        this.studyService.getStudy(studyId)
          .subscribe(study => {
            this.study = this.studyService.initializeProperties(study)
            this.study = this.initializeStudyAdministrativeProperties(study)
            this.updatePageTitle()
            this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study)
          })
      } else {
        this.study = this.studyService.initNew()
      }
    }

    private updatePageTitle():void {
        if(this.study.prefLabel) {
            let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
            let bareTitle:string = this.titleService.getTitle();
            this.titleService.setTitle(translatedLabel + " - " + bareTitle)
        }
    }

    private initializeStudyAdministrativeProperties(study: Study): Study {
        if (study.dataProcessingStartDate) {
          this.dataProcessingStartDate = new Date(study.dataProcessingStartDate)
        }
        if (study.dataProcessingEndDate) {
          this.dataProcessingEndDate = new Date(study.dataProcessingEndDate)
        }
        return study;
    }

    ngAfterContentChecked(): void {
      if (this.studyForm) {
        if (this.studyForm !== this.currentForm) {
          this.currentForm = this.studyForm
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

        if (!this.study.personRegistry) {
          this.study.registryPolicy = null
          this.study.purposeOfPersonRegistry = null
          this.study.personRegistrySources = null
          this.study.personRegisterDataTransfers = null
          this.study.personRegisterDataTransfersOutsideEuOrEea = null
          this.studyService.initializeProperties(this.study)
        }

        if (!(this.study.confidentialityClass === ConfidentialityClass.PARTLY_CONFIDENTIAL || ConfidentialityClass.CONFIDENTIAL)) {
          this.study.groundsForConfidentiality = null
          this.studyService.initializeProperties(this.study)
        }

        if (this.currentForm.invalid) {
          this.growlMessageService.buildAndShowMessage('error',
            'operations.common.save.result.fail.summary',
            'operations.common.save.result.fail.detail')
          this.savingInProgress = false
          this.savingHasFailed = true
          return
        }

        this.study.dataProcessingStartDate = this.dataProcessingStartDate ?
          this.dateUtils.convertToIsoDate(this.dataProcessingStartDate) : null
        this.study.dataProcessingEndDate = this.dataProcessingEndDate ?
          this.dateUtils.convertToIsoDate(this.dataProcessingEndDate) : null

        this.studyService.save(this.study)
            .finally(() => {
              this.savingInProgress = false
            })
            .subscribe(savedStudy => {
                this.study = savedStudy;
                this.goBack();
            });
    }

    goBack() {
        if (this.study.id) {
            this.router.navigate(['/editor/studies', this.study.id, 'administrative-information']);
        } else {
            this.router.navigate(['/editor/studies']);
        }
    }
}
