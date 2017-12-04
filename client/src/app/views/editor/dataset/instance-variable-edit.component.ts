import {ActivatedRoute, Router} from '@angular/router';
import {
  Component, OnInit, ViewChild,
  AfterContentChecked
} from '@angular/core'
import {NgForm} from '@angular/forms'
import {Observable,Subscription} from 'rxjs';
import {SelectItem} from 'primeng/components/common/api';
import {Title} from '@angular/platform-browser'
import {TranslateService} from '@ngx-translate/core';

import {BreadcrumbService} from '../../../services-common/breadcrumb.service'
import {CodeList} from '../../../model2/code-list';
import {CodeListService3} from '../../../services-common/code-list.service';
import {Concept} from '../../../model2/concept';
import {ConceptService} from '../../../services-common/concept.service'
import {Dataset} from '../../../model2/dataset';
import {EditorDatasetService} from '../../../services-editor/editor-dataset.service'
import {EditorStudyService} from '../../../services-editor/editor-study.service'
import {DateUtils} from '../../../utils/date-utils'
import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {InstanceVariable} from '../../../model2/instance-variable';
import {EditorInstanceVariableService} from '../../../services-editor/editor-instance-variable.service'
import {InstanceQuestion} from '../../../model2/instance-question';
import {InstanceQuestionService} from '../../../services-common/instance-question.service'
import {LangPipe} from '../../../utils/lang.pipe';
import {NodeUtils} from '../../../utils/node-utils'
import {Quantity} from '../../../model2/quantity';
import {QuantityService} from '../../../services-common/quantity.service'
import {Study} from '../../../model2/study'
import {StudySidebarActiveSection} from '../study/sidebar/study-sidebar-active-section'
import {Unit} from '../../../model2/unit';
import {UnitService} from '../../../services-common/unit.service'
import {UnitType} from '../../../model2/unit-type'
import {UnitTypeService} from '../../../services-common/unit-type.service'
import {Variable} from '../../../model2/variable'
import {VariableService} from '../../../services-common/variable.service'

@Component({
    templateUrl: './instance-variable-edit.component.html'
})
export class InstanceVariableEditComponent implements OnInit, AfterContentChecked {

    study: Study
    dataset: Dataset
    instanceVariable: InstanceVariable
    language: string

    @ViewChild('instanceVariableForm') instanceVariableForm: NgForm
    currentForm: NgForm
    formErrors: any = {
      'prefLabel': [],
      'referencePeriodStart': [],
      'referencePeriodEnd': []
    }

    conceptSearchSubscription: Subscription
    conceptSearchResults: Concept[] = []
    freeConcepts: string[] = []

    yearRangeForReferencePeriodFields: string =  ('1900:' + (new Date().getFullYear() + 20))
    referencePeriodStart: Date
    referencePeriodEnd: Date

    sourceSearchSubscription: Subscription
    sourceSearchResults: Dataset[] = []

    allQuantityItems: SelectItem[] = []
    newQuantity: Quantity

    allUnitItems: SelectItem[] = []
    newUnit: Unit

    allCodeListItems: SelectItem[]
    viewCodeList: CodeList
    newCodeList: CodeList

    allUnitTypes: UnitType[] = []
    newUnitType: UnitType

    variableSearchSubscription: Subscription
    variableSearchResults: Variable[]

    instanceQuestionSearchSubscription: Subscription
    instanceQuestionSearchResults: InstanceQuestion[] = []
    newInstanceQuestion: InstanceQuestion

    savingInProgress: boolean = false
    savingHasFailed: boolean = false

    readonly sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES

    constructor(
        private studyService: EditorStudyService,
        private datasetService: EditorDatasetService,
        private instanceVariableService: EditorInstanceVariableService,
        private instanceQuestionService: InstanceQuestionService,
        private codeListService: CodeListService3,
        private variableService: VariableService,
        private conceptService: ConceptService,
        private quantityService: QuantityService,
        private unitService: UnitService,
        private unitTypeService: UnitTypeService,
        private growlMessageService: GrowlMessageService,
        private breadcrumbService: BreadcrumbService,
        private titleService: Title,
        private nodeUtils: NodeUtils,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService,
        private langPipe: LangPipe,
        private dateUtils: DateUtils
    ) {
        this.language = translateService.currentLang
    }

    ngOnInit(): void {
      const studyId = this.route.snapshot.params['studyId']
      const datasetId = this.route.snapshot.params['datasetId']
      const instanceVariableId = this.route.snapshot.params['instanceVariableId']

      if (instanceVariableId) {
        Observable.forkJoin(
          this.studyService.getStudy(studyId),
          this.datasetService.getDataset(studyId, datasetId),
          this.instanceVariableService.getInstanceVariable(studyId, datasetId, instanceVariableId)
        ).subscribe(data => {
          this.study = data[0]
          this.dataset = data[1]
          this.instanceVariable = this.initInstanceVariable(data[2])
          this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset, this.instanceVariable)
          this.updatePageTitle()
        })
       }
      else {
        Observable.forkJoin(
          this.studyService.getStudy(studyId),
          this.datasetService.getDataset(studyId, datasetId)
        ).subscribe(data => {
          this.study = data[0]
          this.dataset = data[1]
          this.breadcrumbService.updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset)
        })

        this.instanceVariable = this.initInstanceVariable({
          id: null,
          prefLabel: null,
          description: null,
          referencePeriodStart: null,
          referencePeriodEnd: null,
          technicalName: null,
          conceptsFromScheme: [],
          freeConcepts: null,
          valueDomainType: null,
          quantity: null,
          unit: null,
          qualityStatement: null,
          codeList: null,
          missingValues: null,
          defaultMissingValue: null,
          valueRangeMin: null,
          valueRangeMax: null,
          variable: null,
          partOfGroup: null,
          source: null,
          sourceDescription: null,
          dataType: null,
          unitType: null,
          instanceQuestions: [],
          dataFormat: null,
          dataset: null
        })
      }

      this.getAllQuantitiesAndUnits()
      this.getAllCodeLists()
      this.getAllUnitTypes()
    }

    private initInstanceVariable(instanceVariable: InstanceVariable): InstanceVariable {
      this.initProperties(instanceVariable, [
        'prefLabel',
        'description',
        'freeConcepts',
        'qualityStatement',
        'missingValues',
        'partOfGroup',
        'sourceDescription',
        'dataFormat'
      ])
      if (instanceVariable.freeConcepts && instanceVariable.freeConcepts[this.language]) {
        this.freeConcepts = instanceVariable.freeConcepts[this.language].split(';')
        this.freeConcepts = this.freeConcepts.map(freeConcept => freeConcept.trim())
      }
      if (instanceVariable.referencePeriodStart) {
        this.referencePeriodStart = new Date(instanceVariable.referencePeriodStart)
      }
      if (instanceVariable.referencePeriodEnd) {
        this.referencePeriodEnd = new Date(instanceVariable.referencePeriodEnd)
      }
      return instanceVariable
    }

    private initProperties(node: any, properties: string[]): void {
      this.nodeUtils.initLangValuesProperties(node, properties, [ this.language ])
    }

    private updatePageTitle():void {
      if(this.instanceVariable.prefLabel) {
        let translatedLabel:string = this.langPipe.transform(this.instanceVariable.prefLabel)
        let bareTitle:string = this.titleService.getTitle();
        this.titleService.setTitle(translatedLabel + " - " + bareTitle)
      }
    }

    private getAllQuantitiesAndUnits(): void {
      this.allQuantityItems = []
      this.allUnitItems = []

      Observable.forkJoin(
        this.translateService.get('noQuantity'),
        this.quantityService.getAll(),
        this.translateService.get('noUnit'),
        this.unitService.getAll()
      ).subscribe(data => {
        const noQuantityLabel: string = data[0]
        this.allQuantityItems = []
        this.allQuantityItems.push({
          label: noQuantityLabel,
          value: null
        })
        const quantities: Quantity[] = data[1]
        quantities.forEach(quantity => this.allQuantityItems.push(this.convertToQuantityItem(quantity)))

        const noUnitLabel: string = data[2]
        this.allUnitItems = []
        this.allUnitItems.push({
          label: noUnitLabel,
          value: null
        })
        const units: Unit[] = data[3]
        units.forEach(unit => this.allUnitItems.push(this.convertToUnitItem(unit)))
      })
    }

    private convertToQuantityItem(quantity: Quantity): SelectItem {
      return {
        label: this.langPipe.transform(quantity.prefLabel),
        value: quantity
      }
    }

    private convertToUnitItem(unit: Unit): SelectItem {
      let label = this.langPipe.transform(unit.prefLabel)

      let abbreviation = this.langPipe.transform(unit.symbol)
      if (abbreviation && abbreviation.trim() != '') {
        label += ' ('
        label += this.langPipe.transform(unit.symbol)
        label += ')'
      }

      return {
        label: label,
        value: unit
      }
    }

    private getAllCodeLists(): void {
      this.allCodeListItems = []

      Observable.forkJoin(
        this.translateService.get('noCodeList'),
        this.codeListService.getAll()
      ).subscribe(data => {
        const noCodeListLabel: string = data[0]
        this.allCodeListItems.push({
          label: noCodeListLabel,
          value: null
        })

        const codeLists: CodeList[] = data[1]
        codeLists.forEach(codeList => {
          let codeListItemLabel = codeList.prefLabel[this.language]
          if (codeList.referenceId) {
            codeListItemLabel += ', '
            codeListItemLabel += codeList.referenceId
          }
          if (codeList.owner[this.language]) {
            codeListItemLabel += ', '
            codeListItemLabel += codeList.owner[this.language]
          }
          this.allCodeListItems.push({
            label: codeListItemLabel,
            value: codeList
          })
        })
      })
    }

    private getAllUnitTypes() {
      this.unitTypeService.getAll()
        .subscribe(allUnitTypes => this.allUnitTypes = allUnitTypes)
    }

    ngAfterContentChecked(): void {
      if (this.instanceVariableForm) {
        if (this.instanceVariableForm !== this.currentForm) {
          this.currentForm = this.instanceVariableForm
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

    searchConcept(event: any): void {
      this.conceptSearchResults = []
      if (this.conceptSearchSubscription) {
        // Cancel possible on-going search
        this.conceptSearchSubscription.unsubscribe()
      }

      const searchText: string = event.query

      if (searchText.length >= 3) {
        this.conceptSearchSubscription = this.conceptService.search(searchText)
          .subscribe(concepts => this.conceptSearchResults = concepts)
      }
    }

    searchSource(event: any): void {
          this.sourceSearchResults = []
          if (this.sourceSearchSubscription) {
            // Cancel possible on-going search
            this.sourceSearchSubscription.unsubscribe()
          }

          const searchText: string = event.query

          if (searchText.length >= 3) {
            this.sourceSearchSubscription = this.datasetService.search(searchText)
              .subscribe(datasets => this.sourceSearchResults = datasets)
          }
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

    showAddQuantityModal(): void {
      this.initNewQuantity()
    }

    private initNewQuantity() {
      const quantity = {
        id: null,
        prefLabel: null
      }
      this.initProperties(quantity, ['prefLabel'])
      this.newQuantity = quantity
    }

    saveQuantity(quantity): void {
      this.quantityService.save(quantity)
        .subscribe(savedQuantity => {
          this.getAllQuantitiesAndUnits()
          this.instanceVariable.quantity = savedQuantity
          this.closeAddQuantityModal()
        })
    }

    closeAddQuantityModal(): void {
      this.newQuantity = null
    }

    showAddUnitModal(): void {
      this.initNewUnit()
    }

    private initNewUnit() {
      const unit = {
        id: null,
        prefLabel: null,
        symbol: null
      }
      this.initProperties(unit, ['prefLabel', 'symbol'])
      this.newUnit = unit
    }

    saveUnit(unit): void {
      this.unitService.save(unit)
        .subscribe(savedUnit => {
          this.getAllQuantitiesAndUnits()
          this.instanceVariable.unit = savedUnit
          this.closeAddUnitModal()
        })
    }

    closeAddUnitModal(): void {
      this.newUnit = null
    }

    showAddCodeListModal(): void {
      this.initNewCodeList()
    }

    private initNewCodeList() {
      const codeList = {
        id: null,
        codeListType: null,
        referenceId: null,
        prefLabel: null,
        description: null,
        owner: null,
        codeItems: []
      }
      this.initProperties(codeList, ['prefLabel', 'description', 'owner'])
      this.newCodeList = codeList
    }

    saveCodeList(codeList: CodeList): void {
      this.codeListService.save(this.newCodeList)
        .subscribe(savedCodeList => {
          this.getAllCodeLists()
          this.instanceVariable.codeList = savedCodeList
          this.closeAddCodeListModal()
        })
    }

    closeAddCodeListModal(): void {
      this.newCodeList = null
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
          this.instanceVariable.unitType = savedUnitType
          this.closeAddUnitTypeModal()
        })
    }

    closeAddUnitTypeModal() {
      this.newUnitType = null
    }

    searchVariable(event:any): void {
        const searchText: string = event.query
        if (this.variableSearchSubscription) {
        // Cancel possible on-going search
        this.variableSearchSubscription.unsubscribe()
      }
      this.variableSearchSubscription = this.variableService.search(searchText)
        .subscribe(variables => this.variableSearchResults = variables)
   }

    nullifyEmptyVariable(variable:Variable): Variable {
        // for fixing empty string JSON transport when variable removed ("" not an object)
        let checkedVariable:Variable = !variable ? null : variable
        return checkedVariable
    }

    nullifyEmptySource(){
        if (this.instanceVariable.source != undefined && !this.instanceVariable.source.prefLabel){
            this.instanceVariable.source = null;
        }
    }

    isVariable(variable: any): variable is Variable {
      return  (variable === null || (
              (<Variable>variable).id !== undefined &&
              (<Variable>variable).prefLabel !== undefined &&
              (<Variable>variable).description !== undefined))
    }

    updateVariable(variable: Variable) {
      this.instanceVariable.variable = variable
    }

    saveInstanceVariable(): void {
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

        // trailing white space to separate free concepts in search queries
        this.instanceVariable.freeConcepts[this.language] = this.freeConcepts.join('; ')

        this.instanceVariable.referencePeriodStart = this.referencePeriodStart ?
          this.dateUtils.convertToIsoDate(this.referencePeriodStart) : null
        this.instanceVariable.referencePeriodEnd = this.referencePeriodEnd ?
          this.dateUtils.convertToIsoDate(this.referencePeriodEnd) : null

        this.instanceVariable.variable = this.nullifyEmptyVariable(this.instanceVariable.variable)
        this.instanceVariable.variable = this.isVariable(this.instanceVariable.variable) ? this.instanceVariable.variable : null

        this.nullifyEmptySource();

        this.instanceVariableService.saveInstanceVariable(this.study.id, this.dataset.id, this.instanceVariable)
          .finally(() => {
            this.savingInProgress = false
          })
          .subscribe(instanceVariable => {
            this.initInstanceVariable(instanceVariable)
            this.instanceVariable = instanceVariable
            this.goBack()
          })
    }

    searchInstanceQuestion(event: any): void {
      this.instanceQuestionSearchResults = []
      if (this.instanceQuestionSearchSubscription) {
        // Cancel possible on-going search
        this.instanceQuestionSearchSubscription.unsubscribe()
      }

      const searchText: string = event.query
      const datasetId = this.route.snapshot.params['datasetId']
      const studyId = this.route.snapshot.params['studyId'];

      this.instanceQuestionSearchSubscription = this.instanceQuestionService.searchQuestion(searchText, studyId, datasetId)
        .subscribe(instanceQuestions => {
          this.instanceQuestionSearchResults = instanceQuestions
        })
    }

    showAddInstanceQuestionModal(): void {
      this.initNewInstanceQuestion()
    }

    private initNewInstanceQuestion() {
      let question: InstanceQuestion = {
                id: null,
                prefLabel: null
              }

      this.initProperties(question, ['prefLabel'])
      this.newInstanceQuestion = question
    }

    saveInstanceQuestion(instanceQuestion:InstanceQuestion): void {
      this.instanceQuestionService.save(instanceQuestion)
        .subscribe(savedQuestion => {
          if(!this.instanceVariable.instanceQuestions) { this.instanceVariable.instanceQuestions = []}
          this.instanceVariable.instanceQuestions.push(savedQuestion)
          this.closeAddInstanceQuestionModal()
        })
    }

    closeAddInstanceQuestionModal(): void {
      this.newInstanceQuestion = null
    }

    confirmRemove(): void {
        this.translateService.get('confirmInstanceVariableDelete')
            .subscribe((message: string) => {
                if (confirm(message)) {
                    this.savingInProgress = true

                    this.instanceVariableService.deleteInstanceVariable(this.study.id, this.dataset.id, this.instanceVariable.id)
                      .finally(() => {
                        this.savingInProgress = false
                      })
                      .subscribe(() => {
                        this.goBackToViewDataset()
                      }
                    )
                }
            })
    }

    goBack(): void {
      if (this.route.snapshot.params['instanceVariableId'] || (this.instanceVariable && this.instanceVariable.id)) {
        this.goBackToViewInstanceVariable()
      }
      else {
        this.goBackToViewDataset()
      }
    }

    private goBackToViewInstanceVariable(): void {
      this.router.navigate([
        '/editor/studies',
        this.study.id,
        'datasets',
        this.dataset.id,
        'instanceVariables',
        this.instanceVariable.id])
    }

    private goBackToViewDataset(): void {
      this.router.navigate([
        '/editor/studies/',
        this.study.id,
        'datasets',
        this.dataset.id,
        'instanceVariables'
      ])
    }
}
