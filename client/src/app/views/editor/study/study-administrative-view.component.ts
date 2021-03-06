import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { LangPipe } from '../../../utils/lang.pipe'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'

import { environment as env} from '../../../../environments/environment'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { ConfidentialityClass } from '../../../model2/confidentiality-class'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { LegalBasisForHandlingPersonalData } from '../../../model2/legal-basis-for-handling-personal-data'
import { LegalBasisForHandlingSensitivePersonalData } from '../../../model2/legal-basis-for-handling-sensitive-personal-data'
import { Study } from '../../../model2/study'
import { StudySidebarActiveSection } from './sidebar/study-sidebar-active-section'
import { TypeOfSensitivePersonalData } from '../../../model2/type-of-sensitive-personal-data'
import { PostStudyRetentionOfPersonalData } from '../../../model2/post-study-retention-of-personal-data'
import { GroupOfRegistree } from 'app/model2/group-of-registree';
import { ReceivingGroup } from 'app/model2/receiving-group';
import { StudyFormConfirmationState } from '../../../model2/study-form-confirmation-state'

@Component({
  templateUrl: './study-administrative-view.component.html',
  styleUrls: ['./study-administrative-view.component.css'],
})
export class StudyAdministrativeViewComponent implements OnInit {

  study: Study
  language: string

  sidebarActiveSection = StudySidebarActiveSection.ADMINISTRATIVE_INFORMATION

  groupOfRegistree = GroupOfRegistree
  receivingGroup = ReceivingGroup
  confidentialityClassType = ConfidentialityClass
  legalBasisForHandlingPersonalData = LegalBasisForHandlingPersonalData
  legalBasisForHandlingSensitivePersonalData = LegalBasisForHandlingSensitivePersonalData
  typeOfSensitivePersonalData = TypeOfSensitivePersonalData
  StudyFormConfirmationState = StudyFormConfirmationState;

  printRegisterDescriptionUrl: string
  printPrivacyNoticeUrl: string
  printScientificPrivacyNoticeUrl: string

  constructor(private breadcrumbService: BreadcrumbService,
              private studyService: EditorStudyService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getStudy()
  }

  getStudy() {
    const studyId = this.route.snapshot.params['studyId']
    this.studyService.getStudy(studyId, false)
      .subscribe(study => {
        this.study = study
        this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study)
        this.updatePageTitle()
        this.printRegisterDescriptionUrl = `${env.contextPath}${env.apiPath}/editor/studies/${study.id}/register-description`
        this.printPrivacyNoticeUrl = `${env.contextPath}${env.apiPath}/editor/studies/${study.id}/privacy-notice`
        this.printScientificPrivacyNoticeUrl = `${env.contextPath}${env.apiPath}/editor/studies/${study.id}/scientific-privacy-notice`
      })
  }

  updatePageTitle():void {
    if(this.study.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

  postStudyRetentionDisposeNotSelected(): boolean {
    const selectedValue = this.study.postStudyRetentionOfPersonalData;
    return PostStudyRetentionOfPersonalData.DISPOSE !== selectedValue &&
           PostStudyRetentionOfPersonalData.DISPOSE_SCIENTIFIC !== selectedValue;
  }

  isNonScientificPersonRegistry() {
    return this.study.personRegistry && this.study.isScientificStudy !== true;
  }

  isScientificPersonRegistry() {
    return this.study.personRegistry && this.study.isScientificStudy;
  }

  isPersonRegistry() {
    return this.study.personRegistry;
  }
}
