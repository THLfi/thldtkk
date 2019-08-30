
import {forkJoin as observableForkJoin, Observable} from 'rxjs';

import {finalize} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {AfterContentChecked, Component, OnInit, ViewChild} from '@angular/core'
import {NgForm, AbstractControl} from '@angular/forms'
import {Title} from '@angular/platform-browser'
import {TranslateService} from '@ngx-translate/core';
import {BreadcrumbService} from '../../../services-common/breadcrumb.service'
import {ConfidentialityClass} from '../../../model2/confidentiality-class'
import {DateUtils} from '../../../utils/date-utils'
import {EditorStudyService} from '../../../services-editor/editor-study.service'
import {EditorSystemService} from '../../../services-editor/editor-system.service'
import {EditorSystemRoleService} from '../../../services-editor/editor-system-role.service'
import {GrowlMessageService} from '../../../services-common/growl-message.service'
import { CurrentUserService } from '../../../services-editor/user.service'
import {LangPipe} from '../../../utils/lang.pipe'
import {NodeUtils} from '../../../utils/node-utils';
import {SelectItem} from 'primeng/components/common/api'
import {RetentionPolicy} from '../../../model2/retention-policy';
import {ExistenceForm} from '../../../model2/existence-form';
import {Study} from '../../../model2/study';
import {StudyType} from '../../../model2/study-type';
import {StudySidebarActiveSection} from './sidebar/study-sidebar-active-section'
import {System} from '../../../model2/system';
import {SystemRole} from '../../../model2/system-role';
import {SystemInRole} from '../../../model2/system-in-role';
import { User } from '../../../model2/user';
import {LegalBasisForHandlingPersonalData} from '../../../model2/legal-basis-for-handling-personal-data';
import {LegalBasisForHandlingSensitivePersonalData} from '../../../model2/legal-basis-for-handling-sensitive-personal-data';
import {TypeOfSensitivePersonalData} from '../../../model2/type-of-sensitive-personal-data';
import { Organization } from 'app/model2/organization';
import { OrganizationUnit } from 'app/model2/organization-unit';
import { RoleLabel } from 'app/model2/role-label'
import { OrganizationService } from 'app/services-common/organization.service';
import { AssociatedOrganization } from 'app/model2/associated-organization';
import {PostStudyRetentionOfPersonalData} from '../../../model2/post-study-retention-of-personal-data'
import {StudyFormType} from '../../../model2/study-form-type';
import {StudyFormTypeSpecifier} from '../../../model2/study-form-type-specifier';
import {StudyFormConfirmationState} from '../../../model2/study-form-confirmation-state';
import {StudyForm} from '../../../model2/study-form';
import { GroupOfRegistree } from 'app/model2/group-of-registree';
import { ReceivingGroup } from 'app/model2/receiving-group';
import { ConfirmDialogService } from '../../common/confirm.dialog.service';
import {LangValues} from '../../../model2/lang-values';

@Component({
    templateUrl: './study-administrative-edit.component.html',
    styleUrls: ['./study-administrative-edit.component.css'],
    providers: [LangPipe]
})
export class StudyAdministrativeEditComponent implements OnInit, AfterContentChecked {

    study: Study;
    StudyType = StudyType;
    selectableRetentionOptions: string[] = [];

    yearRangeForDataProcessingFields: string =  ('1900:' + (new Date().getFullYear() + 20))
    dataProcessingStartDate: Date
    dataProcessingEndDate: Date

    retentionPolicies: SelectItem[] = []
    existenceForms: SelectItem[] = []

    @ViewChild('studyForm') studyForm: NgForm
    currentForm: NgForm
    formErrors: any = {}

    language: string;

    savingInProgress = false
    savingHasFailed = false

    sidebarActiveSection = StudySidebarActiveSection.ADMINISTRATIVE_INFORMATION

    allOrganizations: Organization[];
    availableAssociatedOrganizations: Organization[];

    confidentialityClassType = ConfidentialityClass

    allSystemRoles: SystemRole[]
    allSystemItems: SelectItem[]

    systemInRoleForNewSystem: SystemInRole
    newSystem: System

    defaultSystemLinkDescription: string

    legalBasisForHandlingPersonalDataOptions: SelectItem[] = []
    legalBasisForHandlingSensitivePersonalDataOptions: SelectItem[] = []
    groupsOfRegistreesOptions: SelectItem[] = []
    receivingGroupsOptions: SelectItem[] = []
    typeOfSensitivePersonalDataOptions: SelectItem[] = []
    studyFormTypeOptions: SelectItem[] = [];
    private showUnsavedMessage: boolean = true;
    studyFormTypeSpecifierOptions: SelectItem[] = [];
    StudyFormConfirmationState = StudyFormConfirmationState;

    currentUser: User;

    constructor(
        private studyService: EditorStudyService,
        private systemService: EditorSystemService,
        private systemRoleService: EditorSystemRoleService,
        private organizationService: OrganizationService,
        private growlMessageService: GrowlMessageService,
        private route: ActivatedRoute,
        private router: Router,
        private breadcrumbService: BreadcrumbService,
        private translateService: TranslateService,
        private langPipe: LangPipe,
        private titleService: Title,
        private dateUtils: DateUtils,
        private nodeUtils: NodeUtils,
        private confirmDialogService: ConfirmDialogService,
        private currentUserService: CurrentUserService,
    ) {
        this.language = this.translateService.currentLang
    }


    ngOnInit() {
      this.getStudy()
      this.currentUserService.getCurrentUserObservable().subscribe(user => this.currentUser = user);

      this.translateService.get('legalBasisForHandlingPersonalData')
        .subscribe(translations => {
          this.legalBasisForHandlingPersonalDataOptions = Object.keys(LegalBasisForHandlingPersonalData)
            .map(key => {
              return { label: translations[key], value: key }
            })
        })

      this.translateService.get('legalBasisForHandlingSensitivePersonalData')
        .subscribe(translations => {
          this.legalBasisForHandlingSensitivePersonalDataOptions = Object.keys(LegalBasisForHandlingSensitivePersonalData)
            .map(key => {
              return { label: translations[key], value: key }
            })
        })

      this.translateService.get('groupsOfRegistrees')
        .subscribe(translations => {
          this.groupsOfRegistreesOptions = Object.keys(GroupOfRegistree)
            .map(key => {
              return { label: translations[key], value: key }
            })
        })

      this.translateService.get('receivingGroups')
        .subscribe(translations => {
          this.receivingGroupsOptions = Object.keys(ReceivingGroup)
            .map(key => {
              return { label: translations[key], value: key }
            })
        })

      this.translateService.get('typeOfSensitivePersonalData')
        .subscribe(translations => {
          this.typeOfSensitivePersonalDataOptions = Object.keys(TypeOfSensitivePersonalData)
            .map(key => {
              return { label: translations[key], value: key }
            })
        })

      this.translateService.get('editSystemModal.externalLink')
        .subscribe(translation =>  this.defaultSystemLinkDescription = translation)

      this.populateRetentionPolicies()
      this.populateExistenceForms()
      this.populateStudyFormTypes()
      this.populateStudyFormTypeSpecifiers()
      this.getAllSystemRoles()
      this.getAllSystems()
      this.getAllOrganizations()

      window.addEventListener('beforeunload', (event) => {
          if(this.currentForm.form.dirty && this.showUnsavedMessage){
              event.returnValue = 'Are you sure you want to leave?';
          } else {
              return;
          }
        });
    }

    private getStudy() {
      const studyId = this.route.snapshot.params['studyId']
      if (studyId) {
        this.studyService.getStudy(studyId)
          .subscribe(study => {
            this.study = this.studyService.initializeProperties(study)
            this.study = this.initializeStudyAdministrativeProperties(study)
            this.initializeSampleFormMetadata(study);
            this.updatePageTitle()
            this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study)
            this.updateAvailableAssociatedOrganizations()
            this.updateSelectableRetentionOptions()
          })
      } else {
        this.study = this.studyService.initNew()
        this.updateAvailableAssociatedOrganizations()
        this.updateSelectableRetentionOptions()
      }
    }

    private updatePageTitle() {
        if (this.study.prefLabel) {
            const translatedLabel = this.langPipe.transform(this.study.prefLabel)
            const bareTitle = this.titleService.getTitle();
            this.titleService.setTitle(translatedLabel + ' - ' + bareTitle);
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

    private initializeSampleFormMetadata(study: Study) {
      study.studyForms.forEach(studyForm => {
        if (studyForm.retentionPeriod) {
          studyForm['_retentionPeriod'] = new Date(studyForm.retentionPeriod);
        }
        if (studyForm.disposalDate) {
          studyForm['_disposalDate'] = new Date(studyForm.disposalDate);
        }
      })
    }

    private populateRetentionPolicies() {
      for (const policy in RetentionPolicy) {
        this.translateService.get('retentionPolicy.' + policy).subscribe(policyLabel => {
          if (policy === RetentionPolicy.UNDEFINED.toString()) {
            this.retentionPolicies.push({
                label: policyLabel,
                value: null
              })
          } else {
            this.retentionPolicies.push({
              label: policyLabel,
              value: policy
            })
          }
        })
      }
    }

    private populateExistenceForms() {
      for (const form in ExistenceForm) {
        this.translateService.get('existenceForm.' + form).subscribe(formLabel => {
            this.existenceForms.push({
              label: formLabel,
              value: form
            })
        })
      }
    }

    private populateStudyFormTypes() {
      for (const formType in StudyFormType) {
        this.translateService
          .get('studyForm.' + formType)
          .subscribe(label => {
            this.studyFormTypeOptions.push({
              label: label,
              value: formType,
              disabled: formType === StudyFormType.SAMPLE
            });
          });
      }
    }

    private populateStudyFormTypeSpecifiers() {
      for (const typeSpecifier in StudyFormTypeSpecifier) {
        this.translateService
          .get('studyFormTypeSpecifier.' + typeSpecifier)
          .subscribe(label => {
            this.studyFormTypeSpecifierOptions.push({
              label: label,
              value: typeSpecifier
            });
          });
      }
    }

    private getAllSystemRoles() {
      this.systemRoleService.getAll().subscribe(systemRoles =>
        this.allSystemRoles = systemRoles)
    }

    removeSystemInRole(systemInRole: SystemInRole) {
      const index: number = this.study.systemInRoles.indexOf(systemInRole)
      if (index !== -1) {
        this.study.systemInRoles.splice(index, 1)
      }
    }

    removeStudyForm(studyForm: StudyForm) {
      const index = this.study.studyForms.indexOf(studyForm);
      if (index !== -1) {
        this.study.studyForms.splice(index, 1);
      }
    }

    showAddSystemModal(systemInRole: SystemInRole): void {
      this.systemInRoleForNewSystem = systemInRole
      this.initNewSystem()
    }

    private initNewSystem(): void {
      this.newSystem = this.systemService.initNew()
      this.newSystem.ownerOrganization = this.study.ownerOrganization

    }

    saveSystem(event): void {
      if (this.newSystem.link && this.newSystem.link.linkUrl) {
        this.newSystem.link.prefLabel[this.language] = this.defaultSystemLinkDescription
      }

      this.systemService.save(this.newSystem)
        .subscribe(savedSystem => {
          this.getAllSystems()
          if (this.systemInRoleForNewSystem) {
            this.systemInRoleForNewSystem.system = savedSystem
          }
          this.closeAddSystemModal()
        })
    }

    closeAddSystemModal() {
      this.newSystem = null
      this.systemInRoleForNewSystem = null
    }

    private getAllSystems() {
      this.allSystemItems = []

      observableForkJoin(
        this.translateService.get('noSystem'),
        this.systemService.getAll()
      ).subscribe(data => {
        this.allSystemItems.push({
          label: data[0],
          value: null
        })
        data[1].forEach(system => this.allSystemItems.push({
          label: this.langPipe.transform(system.prefLabel),
          value: system
        }))
      })
    }

    private getAllOrganizations() {
      this.organizationService.getAllOrganizations()
        .subscribe(organizations => {
          this.allOrganizations = organizations
          this.updateAvailableAssociatedOrganizations()
        });
    }

    private updateAvailableAssociatedOrganizations() {
      if (this.study && this.allOrganizations) {
        const organizationIds = [];
        if (this.study.ownerOrganization) {
          organizationIds.push(this.study.ownerOrganization.id)
        }

        this.availableAssociatedOrganizations = this.allOrganizations.filter(org => {
          return !organizationIds.includes(org.id)
        })
      }
    }

    addAssociatedOrganization() {
      this.study.associatedOrganizations.push({
        id: null,
        organization: undefined,
        isRegistryOrganization: false
      })
    }

    removeAssociatedOrganization(org: AssociatedOrganization) {
      const idx = this.study.associatedOrganizations.indexOf(org);
      if (idx !== -1) {
        this.study.associatedOrganizations.splice(idx, 1)
      }
      this.updateAvailableAssociatedOrganizations()
    }

    cleanAssociatedOrganizations() {
      this.study.associatedOrganizations = this.study.associatedOrganizations.filter(assoc => {
        return (typeof assoc.organization === 'object');
      })
    }

    addSystemInRole() {
      if (!this.study.systemInRoles) {
          this.study.systemInRoles = []
      }
      const systemInRole = {
        id: null,
        system: null,
        systemRole: null,
      }
      this.study.systemInRoles = [ ...this.study.systemInRoles, systemInRole ]
    }

    addStudyForm() {
      if (! this.study.studyForms) {
        this.study.studyForms = [];
      }

      this.study.studyForms.push({
        id: null,
        type: null,
        typeSpecifier: StudyFormTypeSpecifier.NONE,
        additionalDetails: {} as LangValues
      } as StudyForm);
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

        this.cleanAssociatedOrganizations()
        this.validate()

        if (!this.study.containsSensitivePersonalData) {
          this.study.legalBasisForHandlingSensitivePersonalData = []
          this.study.otherLegalBasisForHandlingSensitivePersonalData = null
          this.study.typeOfSensitivePersonalData = []
          this.study.otherTypeOfSensitivePersonalData = null
          this.studyService.initializeProperties(this.study)
        }

        if (!this.study.personRegistry) {
          this.study.registryPolicy = null
          this.study.purposeOfPersonRegistry = null
          this.study.usageOfPersonalInformation = null;
          this.study.personRegistrySources = null
          this.study.personRegisterDataTransfers = null
          this.study.personRegisterDataTransfersOutsideEuOrEea = null
          this.study.legalBasisForHandlingPersonalData = []
          this.study.otherLegalBasisForHandlingPersonalData = null
          this.study.containsSensitivePersonalData = false
          this.study.legalBasisForHandlingSensitivePersonalData = []
          this.study.otherLegalBasisForHandlingSensitivePersonalData = null
          this.study.typeOfSensitivePersonalData = []
          this.study.otherTypeOfSensitivePersonalData = null
          this.studyService.initializeProperties(this.study)
        }

        if (!(this.study.confidentialityClass === ConfidentialityClass.PARTLY_CONFIDENTIAL || ConfidentialityClass.CONFIDENTIAL)) {
          this.study.groundsForConfidentiality = null
          this.study.securityClassification = null
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

        this.study.dataProcessingStartDate = this.convertDate(this.dataProcessingStartDate);
        this.study.dataProcessingEndDate = this.convertDate(this.dataProcessingEndDate);
        this.study.studyForms.forEach(studyForm => {
          studyForm.retentionPeriod = this.convertDate(studyForm['_retentionPeriod']);
          studyForm.disposalDate = this.convertDate(studyForm['_disposalDate']);
        })

        this.studyService.save(this.study).pipe(
            finalize(() => {
              this.savingInProgress = false
            }))
            .subscribe(savedStudy => {
                this.study = savedStudy;
                this.goBack();
            });
    }

    goBack() {
        this.showUnsavedMessage = false;
        if (this.study.id) {
            this.router.navigate(['/editor/studies', this.study.id, 'administrative-information']);
        } else {
            this.router.navigate(['/editor/studies']);
        }
    }

    private convertDate(date: Date) {
      if (date) {
        return this.dateUtils.convertToIsoDate(date);
      }
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

    private onIsScientificStudyChanged(): void {
      this.study.postStudyRetentionOfPersonalData = undefined
      this.updateSelectableRetentionOptions()
    }

    private updateSelectableRetentionOptions(): void {
      this.selectableRetentionOptions = (<any>Object).values(PostStudyRetentionOfPersonalData)
        .filter(option => this.study.isScientificStudy ? /SCIENTIFIC/.test(option) : !/SCIENTIFIC/.test(option))
    }

    canDeactivate(): Observable<boolean> | boolean {

        let confirmQuestionText: string = '';
        this.translateService.get('confirmExitIfUnsavedChanges').subscribe(translatedText => {
            confirmQuestionText = translatedText;
          });

        if(this.currentForm.form.dirty && this.showUnsavedMessage)
           return this.confirmDialogService.confirm(confirmQuestionText);
        return true;

     }

    onStudyFormTypeChange(studyForm: StudyForm, formType: StudyFormType) {
      studyForm.type = formType;
      if (!this.isTypeSpecifierFieldEnabled(studyForm)) {
        studyForm.typeSpecifier = StudyFormTypeSpecifier.NONE;
      }
    }

    isTypeSpecifierFieldEnabled(studyForm: StudyForm) {
      if (studyForm.type === null) {
        return false;
      }

      let isEnabled = false;

      switch (studyForm.type.valueOf()) {
        case StudyFormType.HUMAN_SAMPLE.valueOf():
          isEnabled = true;
          break;
        case StudyFormType.CELL_SAMPLE.valueOf():
          isEnabled = true;
          break;
        case StudyFormType.MICROBE_SAMPLE.valueOf():
          isEnabled = true;
          break;
        case StudyFormType.ANIMAL_SAMPLE.valueOf():
          isEnabled = true;
          break;
        case StudyFormType.ENVIRONMENTAL_SAMPLE.valueOf():
          isEnabled = true;
          break;
      }

      return isEnabled;
    }

    isUserAdmin(): boolean {
      if (!this.currentUser) {
        return false;
      }

      return this.currentUser.isAdmin;
    }

    isUserHeadOfUnit(organizationUnit: OrganizationUnit): boolean {
      if (!this.currentUser || !this.currentUser.email) {
        return false;
      }

      if (!organizationUnit || !organizationUnit.personInRoles) {
        return false;
      }

      const headOfOrganization =
        organizationUnit.personInRoles.find(pir => pir.role && pir.role.label === RoleLabel.HEAD_OF_ORGANIZATION)

      if (headOfOrganization && headOfOrganization.person.email === this.currentUser.email) {
        return true;
      }
    }
}
