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
import {Dataset} from '../../../model2/dataset';
import {EditorDatasetService} from '../../../services-editor/editor-dataset.service'
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
import {EditorStudyService} from '../../../services-editor/editor-study.service'
import {OrganizationUnit} from "../../../model2/organization-unit";
import {Person} from '../../../model2/person'
import {PersonService} from '../../../services-common/person.service'
import {PersonInRole} from '../../../model2/person-in-role'
import {PopulationService} from '../../../services-common/population.service'
import {Role} from '../../../model2/role'
import {RoleService} from '../../../services-common/role.service'
import {Study} from '../../../model2/study'
import {StudySidebarActiveSection} from '../study/sidebar/study-sidebar-active-section'

import {StringUtils} from '../../../utils/string-utils'
import {UnitType} from "../../../model2/unit-type";
import {UnitTypeService} from '../../../services-common/unit-type.service'
import {Universe} from '../../../model2/universe'
import {UniverseService} from '../../../services-common/universe.service'
import {UsageCondition} from "../../../model2/usage-condition";
import {UsageConditionService} from '../../../services-common/usage-condition.service'

@Component({
    templateUrl: './data-set-edit.component.html',
    providers: [LangPipe]
})
export class DataSetEditComponent implements OnInit, AfterContentChecked {

    study: Study
    dataset: Dataset
    ownerOrganization: Organization
    ownerOrganizationUnit: OrganizationUnit

    @ViewChild('datasetForm') datasetForm: NgForm
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

    // separate type labels and values for multiselect, id of datasetType as value for select
    datasetTypeItems: DatasetTypeItem[] = [];
    selectedDatasetTypeItems: string[] = [];
    datasetTypesById: {[datasetTypeId: string]: DatasetType} = {};

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES

    urlFieldValidatorPattern: string = '[a-zA-Z][a-zA-Z0-9]*:\/\/.*'
    urlSchemeHttpPrefix: string = "http://"
    partiallyValidUrlSchemeExpression: RegExp = /^[a-zA-Z][a-zA-Z0-9]*[:|\/]/ // e.g. 'http:/thl.fi'
    validUrlExpression: RegExp

    constructor(
        private editorStudyService: EditorStudyService,
        private datasetService: EditorDatasetService,
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
        private populationService: PopulationService,
        private universeService: UniverseService,
        private dateUtils: DateUtils,
        private personService: PersonService,
        private roleService: RoleService,
        private userService: CurrentUserService
    ) {
        this.language = this.translateService.currentLang
    }


    ngOnInit() {
        this.getDataset()
        this.getStudy()
        this.validUrlExpression = new RegExp("/^" + this.urlFieldValidatorPattern + "$/")
    }

    private getDataset() {
        const datasetId = this.route.snapshot.params['datasetId'];
        const copyOfDatasetId = this.route.snapshot.queryParams['copyOf'];
        if (datasetId) {
            Observable.forkJoin(
                this.datasetService.getDataset(datasetId)
            ).subscribe(
                data => {
                    this.dataset = this.initializeDatasetProperties(data[0])
                    this.selectedDatasetTypeItems = this.initializeSelectedDatasetTypes(this.dataset);
                    this.updatePageTitle()
                })
        } else if (copyOfDatasetId) {
          this.datasetService.getDataset(copyOfDatasetId).subscribe(data => {
            this.dataset = this.initializeDatasetProperties(data)
            this.selectedDatasetTypeItems = this.initializeSelectedDatasetTypes(this.dataset);
            this.dataset.id = null
            this.dataset.published = false
            this.translateService.get('copy').subscribe(msg => {
              this.dataset.prefLabel[this.language] =
                this.dataset.prefLabel[this.language] + " (" + msg + ")"
            });
            this.dataset.population.id = null
            this.dataset.links.forEach(l => l.id = null)
            this.dataset.personInRoles.forEach(p => p.id = null)
            this.dataset.instanceVariables.forEach(iv => {
              iv.id = null;
              iv.instanceQuestions.forEach(iq => iq.id = null)
            })
            this.updatePageTitle()
          })
        } else {
            this.dataset = this.datasetService.initNew()
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

    private initializeDatasetProperties(dataset: Dataset): Dataset {
        this.datasetService.initializeProperties(dataset)

        if (dataset.referencePeriodStart) {
          this.referencePeriodStart = new Date(dataset.referencePeriodStart)
        }
        if (dataset.referencePeriodEnd) {
          this.referencePeriodEnd = new Date(dataset.referencePeriodEnd)
        }

        if (dataset.collectionStartDate) {
          this.collectionStartDate = new Date(dataset.collectionStartDate)
        }
        if (dataset.collectionEndDate) {
          this.collectionEndDate = new Date(dataset.collectionEndDate)
        }

        if (dataset.ownerOrganizationUnit.length > 0) {
            this.ownerOrganizationUnit = dataset.ownerOrganizationUnit[0];
        }

        if (dataset.freeConcepts && dataset.freeConcepts[this.language]) {
            this.freeConcepts = dataset.freeConcepts[this.language].split(';')
            this.freeConcepts = this.freeConcepts.map(freeConcept => freeConcept.trim())

        }

        return dataset;
    }

    private getStudy() {
      const studyId = this.route.snapshot.params['studyId']
      this.editorStudyService.getStudy(studyId).subscribe(study => {
        this.study = study
        this.ownerOrganization = study.ownerOrganization
      })
    }

    private initializeSelectedDatasetTypes(dataset: Dataset): string[] {
        let storedDatasetTypeItems = []
        dataset.datasetTypes.forEach(datasetType => {storedDatasetTypeItems.push(datasetType.id)});
        return storedDatasetTypeItems;
    }

    private initProperties(node: any, properties: string[]): void {
      this.nodeUtils.initLangValuesProperties(node, properties, [ this.language ])
    }

    private updatePageTitle():void {
        if(this.dataset.prefLabel) {
            let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
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
      if (!this.dataset.personInRoles) {
          this.dataset.personInRoles = []
      }
      const personInRole = {
        id: null,
        person: null,
        role: null,
        public: true
      }
      this.dataset.personInRoles = [ ...this.dataset.personInRoles, personInRole ]
    }

    removePersonInRole(personInRole: PersonInRole) {
      let index: number = this.dataset.personInRoles.indexOf(personInRole)
      if (index !== -1) {
        this.dataset.personInRoles.splice(index, 1)
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
      if (!this.dataset.links) {
          this.dataset.links = []
      }
      const link = {
        id: null,
        prefLabel: null,
        linkUrl: null
      }
      this.initProperties(link, [ 'prefLabel', 'linkUrl' ])
      this.dataset.links = [ ...this.dataset.links, link ]
    }

    removeLink(link) {
        let index: number = this.dataset.links.indexOf(link);
        if (index !== -1) {
            this.dataset.links.splice(index, 1);
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

    importXml(event) {
        this.datasetService.importDataset(event).subscribe(
            data => {
                this.dataset = this.initializeDatasetProperties(data);
            }, error => {
                console.log(error);
            });
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
          this.dataset.unitType = savedUnitType
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
          this.dataset.universe = savedUniverse
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

        this.dataset.referencePeriodStart = this.referencePeriodStart ?
          this.dateUtils.convertToIsoDate(this.referencePeriodStart) : null
        this.dataset.referencePeriodEnd = this.referencePeriodEnd ?
          this.dateUtils.convertToIsoDate(this.referencePeriodEnd) : null

        this.dataset.collectionStartDate = this.collectionStartDate ?
          this.dateUtils.convertToIsoDate(this.collectionStartDate) : null
        this.dataset.collectionEndDate = this.collectionEndDate ?
          this.dateUtils.convertToIsoDate(this.collectionEndDate) : null

        this.dataset.ownerOrganizationUnit = [];

        if (this.ownerOrganization && this.ownerOrganizationUnit) {
            this.dataset.ownerOrganizationUnit.push(this.ownerOrganizationUnit);
        }

        // trailing white space to separate free concepts in search queries
        this.dataset.freeConcepts[this.language] = this.freeConcepts.join('; ')

        this.dataset.datasetTypes = this.resolveSelectedDatasetTypes();

        // Remove empty/null predecessors
        this.dataset.predecessors = this.dataset.predecessors.filter(predecessor => predecessor && predecessor.id)

        this.editorStudyService.saveDataset(this.study.id, this.dataset)            .finally(() => {
              this.savingInProgress = false
            })
            .subscribe(savedDataset => {
                this.dataset = savedDataset;
                this.goBack();
            });
    }

    goBack() {
          this.router.navigate(['/editor/studies', this.study.id, 'datasets', this.dataset.id])
    }
}
