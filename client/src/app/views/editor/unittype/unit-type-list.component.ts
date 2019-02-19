import { Component, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ConfirmationService } from 'primeng/primeng'
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from '../../../services-common/growl-message.service'

import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { LangPipe } from '../../../utils/lang.pipe'
import { Study } from '../../../model2/study'
import { UnitTypeService } from '../../../services-common/unit-type.service'
import { UnitType } from '../../../model2/unit-type'

import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import 'rxjs/add/observable/of';

@Component({
  templateUrl: './unit-type-list.component.html',
  styleUrls: ['./unit-type-list.component.css']
})
export class UnitTypeListComponent implements OnInit {

  unitTypes: UnitType[] = []
  editUnitType: UnitType

  searchTerms: Subject<string>
  searchInput: string

  loadingUnitTypes: boolean

  readonly searchDelay = 300;
  readonly unitTypeRemoveConfirmationKey: string = 'confirmRemoveUnitTypeModal.message'
  readonly unitTypeSaveSuccessKey: string = 'operations.unitType.save.result.success'

  constructor(
    private unitTypeService: UnitTypeService,
    private confirmationService: ConfirmationService,
    private translateService: TranslateService,
    private langPipe: LangPipe,
    private growlMessageService: GrowlMessageService
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit(): void {
    this.loadingUnitTypes = true
    this.unitTypeService.getAll()
      .subscribe(unitTypes => {
        this.unitTypes = unitTypes
        this.loadingUnitTypes = false
      })
    this.initSearchSubscription(this.searchTerms)
  }

  showEditUnitTypeModal(unitType: UnitType) {
    this.editUnitType = unitType
  }

  closeEditUnitTypeModal() {
    this.editUnitType = null
    this.refreshUnitTypes()
  }

  saveUnitType(unitType: UnitType) {
    this.unitTypeService.save(unitType).subscribe(() => {
      this.editUnitType = null
      this.refreshUnitTypes()
      this.growlMessageService.buildAndShowMessage('success', this.unitTypeSaveSuccessKey)
    })
  }

  showAddNewUnitTypeModal(): void {
    this.editUnitType = this.unitTypeService.initNew()
  }

  confirmRemoveUnitType(unitType: UnitType): void {
    Observable.forkJoin(
      this.unitTypeService.getUnitTypeDatasets(unitType),
      this.unitTypeService.getUnitTypeInstanceVariables(unitType),
      this.unitTypeService.getUnitTypeStudies(unitType)
    ).subscribe(data => {
      let datasets: Dataset[] = data[0]
      let instanceVariables: InstanceVariable[] = data[1]
      let studies: Study[] = data[2]

      let translatedLabel: string = this.langPipe.transform(unitType.prefLabel)
      let translationParams: {} = {
        unitType: translatedLabel,
        datasetCount: datasets.length,
        publishedDatasetCount: datasets.filter(dataset => dataset.published).length,
        instanceVariableCount: instanceVariables.length,
        publishedInstanceVariableCount: instanceVariables.filter(variable => variable.published).length,
        studyCount: studies.length,
        publishedStudyCount: studies.filter(study => study.published).length
      }

      this.translateService.get(this.unitTypeRemoveConfirmationKey, translationParams).subscribe(confirmationMessage => {
        this.confirmationService.confirm({
          message: confirmationMessage,
          accept: () => {
            this.unitTypeService.delete(unitType).subscribe(() => this.refreshUnitTypes())
          }
        })
      })
    })
  }

  refreshUnitTypes(): void {
    this.instantSearchUnitTypes(this.searchInput)
  }

  searchUnitTypes(literalSearchTerms: string): void {
    this.searchTerms.next(literalSearchTerms)
  }

  instantSearchUnitTypes(literalSearchTerms: string): void {
    this.loadingUnitTypes = true
    this.unitTypeService.search(literalSearchTerms).subscribe(unitTypes => {
      this.unitTypes = unitTypes
      this.loadingUnitTypes = false
    })
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.debounceTime(this.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.loadingUnitTypes = true;
        return this.unitTypeService.search(term)
      })
      .catch(error => {
        this.initSearchSubscription(searchTerms)
        return Observable.of<UnitType[]>([])
      })
      .subscribe(unitTypes => {
        this.unitTypes = unitTypes
        this.loadingUnitTypes = false
      })
  }

}
