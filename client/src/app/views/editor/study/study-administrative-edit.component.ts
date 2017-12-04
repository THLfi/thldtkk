import {ActivatedRoute, Router} from '@angular/router';
import {
  Component, OnInit, ViewChild,
  AfterContentChecked
} from '@angular/core'
import {NgForm, AbstractControl} from '@angular/forms'
import {Observable} from 'rxjs'
import {Title} from '@angular/platform-browser'
import {TranslateService} from '@ngx-translate/core';
import { BreadcrumbService } from '../../../services-common/breadcrumb.service'

import {Study} from '../../../model2/study';
import {EditorStudyService} from '../../../services-editor/editor-study.service'
import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {LangPipe} from '../../../utils/lang.pipe'
import {StudySidebarActiveSection} from './sidebar/study-sidebar-active-section'

@Component({
    templateUrl: './study-administrative-edit.component.html',
    providers: [LangPipe]
})
export class StudyAdministrativeEditComponent implements OnInit, AfterContentChecked {

    study: Study;

    @ViewChild('studyForm') studyForm: NgForm
    currentForm: NgForm
    formErrors: any = {}

    language: string;

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    sidebarActiveSection = StudySidebarActiveSection.ADMINISTRATIVE_INFORMATION

    constructor(
        private studyService: EditorStudyService,
        private growlMessageService: GrowlMessageService,
        private route: ActivatedRoute,
        private router: Router,
        private breadcrumbService: BreadcrumbService,
        private translateService: TranslateService,
        private langPipe: LangPipe,
        private titleService: Title
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
            this.updatePageTitle()
            this.breadcrumbService.updateBreadcrumbsForStudyDatasetAndInstanceVariable(this.study)
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

        if (!this.study.personRegistry || this.study.personRegistry !== 'true'){
          this.study.purposeOfPersonRegistry = null
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
