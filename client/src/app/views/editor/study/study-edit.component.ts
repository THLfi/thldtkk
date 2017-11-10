import {ActivatedRoute, Router} from '@angular/router';
import {
  Component, OnInit, ViewChild,
  AfterContentChecked
} from '@angular/core'
import {NgForm, AbstractControl} from '@angular/forms'
import {Observable, Subscription} from 'rxjs';
import {SelectItem} from 'primeng/components/common/api'
import {Title} from '@angular/platform-browser'
import {TranslateService} from '@ngx-translate/core';
import {TruncateCharactersPipe} from 'ng2-truncate/dist/truncate-characters.pipe'

import {Concept} from '../../../model2/concept';
import {ConceptService} from '../../../services-common/concept.service';
import {CurrentUserService} from '../../../services-editor/user.service'
import {Study} from '../../../model2/study';
import {EditorStudyService} from '../../../services-editor/editor-study.service'
import {DatasetType} from '../../../model2/dataset-type'
import {DatasetTypeItem} from '../../../model2/dataset-type-item'
import {DatasetTypeService} from '../../../services-common/dataset-type.service'
import {DateUtils} from '../../../utils/date-utils'
import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {LangPipe} from '../../../utils/lang.pipe'
import {LifecyclePhase} from "../../../model2/lifecycle-phase";
import {LifecyclePhaseService} from '../../../services-common/lifecycle-phase.service'
import {Link} from "../../../model2/link";
import {NodeUtils} from '../../../utils/node-utils';
import {Organization} from "../../../model2/organization";
import {OrganizationService} from '../../../services-common/organization.service'
import {OrganizationUnit} from "../../../model2/organization-unit";
import {Person} from '../../../model2/person'
import {PersonService} from '../../../services-common/person.service'
import {PersonInRole} from '../../../model2/person-in-role'
import {Role} from '../../../model2/role'
import {RoleService} from '../../../services-common/role.service'
import {StudySidebarActiveSection} from './sidebar/study-sidebar-active-section'
import {StringUtils} from '../../../utils/string-utils'
import {UnitType} from "../../../model2/unit-type";
import {UnitTypeService} from '../../../services-common/unit-type.service'
import {Universe} from '../../../model2/universe'
import {UniverseService} from '../../../services-common/universe.service'
import {UsageCondition} from "../../../model2/usage-condition";
import {UsageConditionService} from '../../../services-common/usage-condition.service'
import { BreadcrumbService } from '../../../services-common/breadcrumb.service'

@Component({
    templateUrl: './study-edit.component.html',
    providers: [LangPipe]
})
export class StudyEditComponent implements OnInit, AfterContentChecked {

    study: Study;
    ownerOrganizationUnit: OrganizationUnit;

    @ViewChild('studyForm') studyForm: NgForm
    currentForm: NgForm
    formErrors: any = {}

    yearRangeForReferencePeriodFields: string =  ('1900:' + (new Date().getFullYear() + 20))
    referencePeriodStart: Date
    referencePeriodEnd: Date

    collectionStartDate: Date
    collectionEndDate: Date

    allLifecyclePhases: LifecyclePhase[];
    availableOrganizations: Organization[];

    organizationUnitsOfOrganization: {[organizatioId: string]: OrganizationUnit[]} = {}

    allPersonItems: SelectItem[]
    allRoles: Role[]

    personInRoleForNewPerson: PersonInRole
    newPerson: Person

    allUsageConditions: UsageCondition[];
    language: string;
    lifecyclePhase: LifecyclePhase;
    conceptSearchSubscription: Subscription;
    conceptSearchResults: Concept[] = [];
    freeConcepts: string[] = [];

    allUnitTypes: UnitType[] = []
    newUnitType: UnitType

    allUniverseItems: SelectItem[] = []
    newUniverse: Universe

    datasetTypeItems: DatasetTypeItem[] = [];
    selectedDatasetTypeItems: string[] = [];
    datasetTypesById: {[datasetTypeId: string]: DatasetType} = {};

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    sidebarActiveSection = StudySidebarActiveSection.STUDY

    urlFieldValidatorPattern: string = '[a-zA-Z][a-zA-Z0-9]*:\/\/.*'
    urlSchemeHttpPrefix: string = "http://"
    partiallyValidUrlSchemeExpression: RegExp = /^[a-zA-Z][a-zA-Z0-9]*[:|\/]/ // e.g. 'http:/thl.fi'
    validUrlExpression: RegExp

    constructor(
        private editorStudyService: EditorStudyService,
        private lifecyclePhaseService: LifecyclePhaseService,
        private nodeUtils: NodeUtils,
        private organizationService: OrganizationService,
        private growlMessageService: GrowlMessageService,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService,
        private unitTypeService: UnitTypeService,
        private usageConditionService: UsageConditionService,
        private datasetTypeService: DatasetTypeService,
        private conceptService: ConceptService,
        private langPipe: LangPipe,
        private truncatePipe: TruncateCharactersPipe,
        private titleService: Title,
        private universeService: UniverseService,
        private dateUtils: DateUtils,
        private personService: PersonService,
        private roleService: RoleService,
        private userService: CurrentUserService,
        private breadcrumbService: BreadcrumbService
    ) {
        this.language = this.translateService.currentLang
    }


    ngOnInit() {
        this.getStudy();
        this.validUrlExpression = new RegExp("/^" + this.urlFieldValidatorPattern + "$/")
    }

    private getStudy() {
        const studyId = this.route.snapshot.params['id'];
        const copyOfStudyId = this.route.snapshot.queryParams['copyOf'];

        if (studyId) {
            Observable.forkJoin(
                this.editorStudyService.getStudy(studyId)
            ).subscribe(
                data => {
                    this.study = this.initializeStudyProperties(data[0])
                    this.selectedDatasetTypeItems = this.initializeSelectedDatasetTypes(this.study);
                    this.updatePageTitle()
                    this.breadcrumbService.updateBreadcrumbsForStudyDatasetAndInstanceVariable(this.study)
                })
        } else if (copyOfStudyId) {
          this.editorStudyService.getStudy(copyOfStudyId).subscribe(data => {
            this.study = this.initializeStudyProperties(data)
            this.selectedDatasetTypeItems = this.initializeSelectedDatasetTypes(this.study);
            this.study.id = null
            this.study.published = false
            this.translateService.get('copy').subscribe(msg => {
              this.study.prefLabel[this.language] =
                this.study.prefLabel[this.language] + " (" + msg + ")"
            });
            this.study.population.id = null
            this.study.links.forEach(l => l.id = null)
            this.study.personInRoles.forEach(p => p.id = null)
            this.updatePageTitle()
          })
        } else {
            this.study = this.editorStudyService.initNew()
        }

        this.getAvailableOrganizations()
        this.getAllPersons()
        this.getAllRoles()
        this.lifecyclePhaseService.getAll()
            .subscribe(lifecyclePhases => this.allLifecyclePhases = lifecyclePhases)
        this.usageConditionService.getAll()
            .subscribe(usageConditions => this.allUsageConditions = usageConditions)
        this.getAllUnitTypes()
        this.getAllUniverses()

        this.datasetTypeService.getAll()
          .subscribe(datasetTypes => {
            datasetTypes.forEach(datasetType => {
              let translatedTypeLabel = this.langPipe.transform(datasetType.prefLabel)
              this.datasetTypeItems.push(new DatasetTypeItem(translatedTypeLabel, datasetType.id))
              this.datasetTypesById[datasetType.id] = datasetType
            })
          })
    }

    private initializeStudyProperties(study: Study): Study {
        this.editorStudyService.initializeProperties(study)

        if (study.referencePeriodStart) {
          this.referencePeriodStart = new Date(study.referencePeriodStart)
        }
        if (study.referencePeriodEnd) {
          this.referencePeriodEnd = new Date(study.referencePeriodEnd)
        }

        if (study.collectionStartDate) {
          this.collectionStartDate = new Date(study.collectionStartDate)
        }
        if (study.collectionEndDate) {
          this.collectionEndDate = new Date(study.collectionEndDate)
        }

        if (study.ownerOrganizationUnit) {
            this.ownerOrganizationUnit = study.ownerOrganizationUnit;
        }

        if (study.freeConcepts && study.freeConcepts[this.language]) {
            this.freeConcepts = study.freeConcepts[this.language].split(';')
            this.freeConcepts = this.freeConcepts.map(freeConcept => freeConcept.trim())
        }

        return study;
    }

    private initializeSelectedDatasetTypes(study: Study): string[] {
        let storedDatasetTypeItems = []
        study.datasetTypes.forEach(datasetType => {storedDatasetTypeItems.push(datasetType.id)});
        return storedDatasetTypeItems;
    }

    private initProperties(node: any, properties: string[]): void {
      this.nodeUtils.initLangValuesProperties(node, properties, [ this.language ])
    }

    private updatePageTitle():void {
        if(this.study.prefLabel) {
            let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
            let bareTitle:string = this.titleService.getTitle();
            this.titleService.setTitle(translatedLabel + " - " + bareTitle)
        }
    }

    private getAvailableOrganizations() {
      this.userService.isUserAdmin()
        .subscribe(isAdmin => {
          if (isAdmin) {
              this.organizationService.getAllOrganizations()
              .subscribe(organizations => {
                this.availableOrganizations = organizations
                this.extractOrganizationUnits(organizations)
              })
          }
          else {
              this.userService.getUserOrganizations()
              .subscribe(organizations => {
                this.availableOrganizations = organizations
                this.extractOrganizationUnits(organizations)
              })
          }
        })
    }

    private extractOrganizationUnits(organizations: Organization[]) {
      organizations.map(organization => this.organizationUnitsOfOrganization[organization.id] = organization.organizationUnit)
    }

    public onOrganizationChange() {
      this.ownerOrganizationUnit = null;
    }

    private getAllPersons() {
      this.allPersonItems = []

      Observable.forkJoin(
        this.translateService.get('noPerson'),
        this.personService.getAll()
      ).subscribe(data => {
        this.allPersonItems.push({
          label: data[0],
          value: null
        })
        data[1].forEach(person => this.allPersonItems.push(this.convertToPersonItem(person)))
      })
    }

    private convertToPersonItem(person: Person): SelectItem {
      let label = person.firstName

      if (StringUtils.isNotBlank(person.lastName)) {
        label += ' '
        label += person.lastName
      }
      if (StringUtils.isNotBlank(person.email)) {
        label += ', '
        label += person.email
      }

      return {
        label: label,
        value: person
      }
    }

    private getAllRoles() {
      this.roleService.getAll()
        .subscribe(roles => this.allRoles = roles)
    }

    private getAllUnitTypes() {
      this.unitTypeService.getAll()
        .subscribe(allUnitTypes => this.allUnitTypes = allUnitTypes)
    }

    private getAllUniverses() {
      this.allUniverseItems = []

      Observable.forkJoin(
        this.translateService.get('noUniverse'),
        this.universeService.getAll()
      ).subscribe(data => {
        this.allUniverseItems.push({
          label: data[0],
          value: null
        })

        data[1].forEach(universe => this.allUniverseItems.push(this.convertToUniverseItem(universe)))
      })
    }

    private convertToUniverseItem(universe: Universe): SelectItem {
      let label = this.langPipe.transform(universe.prefLabel);

      let description = this.langPipe.transform(universe.description)
      if (StringUtils.isNotBlank(description)) {
        label += (' - ' + this.truncatePipe.transform(description, 50))
      }

      return {
        label: label,
        value: universe
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

    searchConcept(event: any): void {
        const searchText: string = event.query
        if (this.conceptSearchSubscription) {
            // Cancel possible on-going search
            this.conceptSearchSubscription.unsubscribe()
        }
        this.conceptSearchSubscription = this.conceptService.search(searchText)
            .subscribe(concepts => this.conceptSearchResults = concepts)
    }

    getConceptLanguages(concept: Concept): any {
        const languages = []
        for (let lang in concept.prefLabel) {
            if (concept.prefLabel.hasOwnProperty(lang) && lang != this.language) {
                languages.push(lang)
            }
        }
        return languages
    }

    addPersonInRole() {
      if (!this.study.personInRoles) {
          this.study.personInRoles = []
      }
      const personInRole = {
        id: null,
        person: null,
        role: null,
        public: true
      }
      this.study.personInRoles = [ ...this.study.personInRoles, personInRole ]
    }

    removePersonInRole(personInRole: PersonInRole) {
      let index: number = this.study.personInRoles.indexOf(personInRole)
      if (index !== -1) {
        this.study.personInRoles.splice(index, 1)
      }
    }

    showAddPersonModal(personInRole: PersonInRole): void {
      this.personInRoleForNewPerson = personInRole
      this.initNewPerson()
    }

    private initNewPerson(): void {
      this.newPerson = this.personService.initNew()
    }

    savePerson(event): void {
      this.personService.save(this.newPerson)
        .subscribe(savedPerson => {
          this.getAllPersons()
          if (this.personInRoleForNewPerson) {
            this.personInRoleForNewPerson.person = savedPerson
          }
          this.closeAddPersonModal()
        })
    }

    closeAddPersonModal() {
      this.newPerson = null
      this.personInRoleForNewPerson = null
    }

    addLink() {
      if (!this.study.links) {
          this.study.links = []
      }
      const link = {
        id: null,
        prefLabel: null,
        linkUrl: null
      }
      this.initProperties(link, [ 'prefLabel', 'linkUrl' ])
      this.study.links = [ ...this.study.links, link ]
    }

    removeLink(link) {
        let index: number = this.study.links.indexOf(link);
        if (index !== -1) {
            this.study.links.splice(index, 1);
        }
    }

    correctLinkUrlFormat(link: Link): void {
      let linkUrl: string = link.linkUrl[this.language]

      let isValidUrl: boolean = this.validUrlExpression.test(linkUrl)
      let isPartiallyValidUrlScheme: boolean = this.partiallyValidUrlSchemeExpression.test(linkUrl)

      link.linkUrl[this.language] = isValidUrl || (!isValidUrl && isPartiallyValidUrlScheme) ? linkUrl :  this.urlSchemeHttpPrefix+linkUrl
    }

    private resolveSelectedDatasetTypes(): DatasetType[] {

        let selectedDatasetTypes: Array<DatasetType> = [];

        if (this.selectedDatasetTypeItems) {
            this.selectedDatasetTypeItems.forEach(datasetTypeId => {
                let datasetType: DatasetType = this.datasetTypesById[datasetTypeId];
                selectedDatasetTypes.push(datasetType);
            });
        }

        return selectedDatasetTypes;
    }

    showAddUnitTypeModal(): void {
      this.initNewUnitType()
    }

    private initNewUnitType(): void {
      this.newUnitType = this.unitTypeService.initNew()
    }

    saveUnitType(): void {
      this.unitTypeService.save(this.newUnitType)
        .subscribe(savedUnitType => {
          this.getAllUnitTypes()
          this.study.unitType = savedUnitType
          this.closeAddUnitTypeModal()
        })
    }

    closeAddUnitTypeModal() {
      this.newUnitType = null
    }

    showAddUniverseModal(): void {
      this.initNewUniverse()
    }

    private initNewUniverse(): void {
      this.newUniverse = this.universeService.initNew()
    }

    saveUniverse(): void {
      this.universeService.save(this.newUniverse)
        .subscribe(savedUniverse => {
          this.getAllUniverses()
          this.study.universe = savedUniverse
          this.closeAddUniverseModal()
        })
    }

    closeAddUniverseModal() {
      this.newUniverse = null
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

        this.study.referencePeriodStart = this.referencePeriodStart ?
          this.dateUtils.convertToIsoDate(this.referencePeriodStart) : null
        this.study.referencePeriodEnd = this.referencePeriodEnd ?
          this.dateUtils.convertToIsoDate(this.referencePeriodEnd) : null

        this.study.collectionStartDate = this.collectionStartDate ?
          this.dateUtils.convertToIsoDate(this.collectionStartDate) : null
        this.study.collectionEndDate = this.collectionEndDate ?
          this.dateUtils.convertToIsoDate(this.collectionEndDate) : null

        this.study.ownerOrganizationUnit = this.study.ownerOrganization && this.ownerOrganizationUnit ? this.ownerOrganizationUnit : null

        // trailing white space to separate free concepts in search queries
        this.study.freeConcepts[this.language] = this.freeConcepts.join('; ')

        this.study.datasetTypes = this.resolveSelectedDatasetTypes();

        // Remove empty/null predecessors
        this.study.predecessors = this.study.predecessors.filter(predecessor => predecessor && predecessor.id)

        this.editorStudyService.save(this.study)
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
            this.router.navigate(['/editor/studies', this.study.id]);
        } else {
            this.router.navigate(['/editor/studies']);
        }
    }
}