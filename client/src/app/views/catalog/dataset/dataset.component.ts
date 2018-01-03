import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'
import { Observable } from 'rxjs'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableReferencePeriod } from './instance-variable-reference-period'
import { LangPipe } from '../../../utils/lang.pipe'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'
import { Study } from '../../../model2/study'
import { StringUtils } from '../../../utils/string-utils'
import { DatasetDetailHighlight } from './dataset-detail-highlight'

class InstanceVariableWrapper {

  constructor(
    public instanceVariable: InstanceVariable,
    public referencePeriod: InstanceVariableReferencePeriod
  ) { }

}

@Component({
  templateUrl: './dataset.component.html',
  styleUrls: ['./dataset.component.css']
})
export class DatasetComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  referencePeriodStart: string
  referencePeriodEnd: string
  referencePeriodInheritedFromStudy: boolean

  allWrappedInstanceVariables: InstanceVariableWrapper[] = []
  filteredInstanceVariables: InstanceVariableWrapper[] = []
  groupedInstanceVariables: { [instanceVariableGroupName: string]: InstanceVariableWrapper[] } = {}

  primaryHighlights: DatasetDetailHighlight[] = []
  secondaryHighlights: DatasetDetailHighlight[] = []
  DatasetDetailHighlight = DatasetDetailHighlight

  instanceVariableGroupNames: string[] = []
  defaultInstanceVariableGroupName: string
  
  showInstanceVariableFilters: boolean
  selectedInstanceVariableFilterGroups: string[] = []

  readonly secondaryHiglightFontResizeThreshold: number = 9
  unitTypeLabelFontResizeThresholdExceeded: boolean

  constructor(
    private studyService: PublicStudyService,
    private datasetService: PublicDatasetService,
    private instanceVariableService: PublicInstanceVariableService,
    private breadcrumbService: BreadcrumbService,
    private langPipe: LangPipe,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title
  ) {
    this.language = this.translateService.currentLang
    this.defaultInstanceVariableGroupName = 'catalog.dataset.defaultInstanceVariableGroupName'
    this.showInstanceVariableFilters = false
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.updateDataset(params['studyId'], params['datasetId'])
    })
  }

  private updateDataset(studyId: string, datasetId: string): void {
    this.study = null
    this.dataset = null
    this.allWrappedInstanceVariables = []

    Observable.forkJoin(
      this.studyService.getStudy(studyId),
      this.datasetService.getDataset(studyId, datasetId),
      this.translateService.get(this.defaultInstanceVariableGroupName)
    ).subscribe(data => {
      this.study = data[0]
      this.dataset = data[1]
      this.defaultInstanceVariableGroupName = data[2]
      this.updatePageTitle()
      this.breadcrumbService.updateCatalogBreadcrumbsForStudyDatasetAndInstanceVariable(this.study, this.dataset)
      this.updateReferencePeriod()
      this.updateWrappedInstanceVariables()
      this.groupInstanceVariables()
      this.resetInstanceVariableGroupFilters()
      this.pickHighlightedDetails()
      this.resolveUnitTypeLabelLength()
    })
  }

  private updatePageTitle(): void {
    if (this.dataset.prefLabel) {
      let translatedLabel: string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle: string = this.titleService.getTitle()
      this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
    }
  }

  private updateReferencePeriod() {
    this.referencePeriodInheritedFromStudy = false

    if (this.dataset.referencePeriodStart || this.dataset.referencePeriodEnd) {
      this.referencePeriodStart = this.dataset.referencePeriodStart
      this.referencePeriodEnd = this.dataset.referencePeriodEnd
    }
    else if (this.study.referencePeriodStart || this.study.referencePeriodEnd) {
      this.referencePeriodStart = this.study.referencePeriodStart
      this.referencePeriodEnd = this.study.referencePeriodEnd
      this.referencePeriodInheritedFromStudy = true
    }
    else {
      this.referencePeriodStart = null
      this.referencePeriodEnd = null
    }
  }

  updateWrappedInstanceVariables() {
    let wrappers: InstanceVariableWrapper[] = []
    this.dataset.instanceVariables
      .forEach(iv => {
        const referencePeriod = new InstanceVariableReferencePeriod(this.study, this.dataset, iv)
        wrappers = [...wrappers, new InstanceVariableWrapper(iv, referencePeriod)]
      })
    this.allWrappedInstanceVariables = wrappers
  }

  composeInstanceVariableExportUrl(): string {
    return this.instanceVariableService.getInstanceVariableAsCsvExportPath(this.study.id, this.dataset.id)
  }

  pickHighlightedDetails() {
    // primary highlights
    if (this.dataset.universe) {
      this.primaryHighlights.push(DatasetDetailHighlight.UNIVERSE)
    }

    if ((this.dataset.referencePeriodStart || this.dataset.referencePeriodEnd)
      || (this.study.referencePeriodStart || this.study.referencePeriodEnd)) {
      this.primaryHighlights.push(DatasetDetailHighlight.REFERENCE_PERIOD)
    }

    if (this.dataset.population && this.dataset.population.geographicalCoverage) {
      let coverageInCurrentLang: string = this.langPipe.transform(this.dataset.population.geographicalCoverage)

      if (StringUtils.isNotEmpty(coverageInCurrentLang)) {
        this.primaryHighlights.push(DatasetDetailHighlight.GEOGRAPHICAL_COVERAGE)
      }
    }

    // secondary highlights
    if (this.dataset.unitType || this.study.unitType) {
      this.secondaryHighlights.push(DatasetDetailHighlight.UNIT_TYPE)
    }

    if (this.dataset.numberOfObservationUnits) {
      this.secondaryHighlights.push(DatasetDetailHighlight.NUMBER_OF_OBSERVATION_UNITS)
    }

    if (this.dataset.lifecyclePhase) {
      this.secondaryHighlights.push(DatasetDetailHighlight.LIFECYCLE_PHASE)
    }
  }

  groupInstanceVariables() {
    this.allWrappedInstanceVariables.forEach(wrappedInstanceVariable => {
      let groupName: string = this.langPipe.transform(wrappedInstanceVariable.instanceVariable.partOfGroup)
      groupName = StringUtils.isNotEmpty(groupName) ? groupName : this.defaultInstanceVariableGroupName

      if (this.instanceVariableGroupNames.indexOf(groupName) == -1) {
        this.instanceVariableGroupNames.push(groupName)
      }

      let existingInstanceVariables: InstanceVariableWrapper[] = this.groupedInstanceVariables[groupName] ? this.groupedInstanceVariables[groupName] : []

      this.groupedInstanceVariables[groupName] = [...existingInstanceVariables, wrappedInstanceVariable]
    })

    this.instanceVariableGroupNames = this.instanceVariableGroupNames.sort()
    
    let defaultGroupNameIndex = this.instanceVariableGroupNames.indexOf(this.defaultInstanceVariableGroupName)
    
    // default group as last group 
    if(defaultGroupNameIndex != -1) {
      this.instanceVariableGroupNames.splice(defaultGroupNameIndex, 1)
      this.instanceVariableGroupNames.push(this.defaultInstanceVariableGroupName)
    }
    
  }

  filterInstanceVariables() {
    let allGroupsSelected: boolean = this.selectedInstanceVariableFilterGroups.length == this.instanceVariableGroupNames.length

    if (allGroupsSelected) {
      this.filteredInstanceVariables = this.allWrappedInstanceVariables
    }

    else {
      this.filteredInstanceVariables = []

      this.selectedInstanceVariableFilterGroups.forEach(groupName => {
        this.groupedInstanceVariables[groupName]
          .map(wrappedInstanceVariable => this.filteredInstanceVariables.push(wrappedInstanceVariable))
      })
    }
  }

  resetInstanceVariableGroupFilters() {
    this.selectedInstanceVariableFilterGroups = this.instanceVariableGroupNames
    this.filterInstanceVariables()
  }

  deselectInstanceVariableGroupFilters() {
    this.selectedInstanceVariableFilterGroups = []
    this.filterInstanceVariables()
  }

  toggleShowInstanceVariableFilters() {
    this.showInstanceVariableFilters = !this.showInstanceVariableFilters 
  }

  resolveUnitTypeLabelLength() {
    let unitTypeLabel: string = this.dataset.unitType ? 
      this.langPipe.transform(this.dataset.unitType.prefLabel) :
      this.study.unitType ? this.langPipe.transform(this.study.unitType.prefLabel) : ""

    this.unitTypeLabelFontResizeThresholdExceeded = unitTypeLabel && unitTypeLabel.length > this.secondaryHiglightFontResizeThreshold ? true : false
  }
}
