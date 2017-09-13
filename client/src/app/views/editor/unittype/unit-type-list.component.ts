import { Component, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ConfirmationService, Confirmation } from 'primeng/primeng';
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from '../../../services2/growl-message.service'

import { LangPipe } from '../../../utils/lang.pipe'
import { UnitTypeService } from "../../../services2/unit-type.service";
import { UnitType } from "../../../model2/unit-type";
import { Dataset } from "../../../model2/dataset";
import { InstanceVariable } from "../../../model2/instance-variable";

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
    this.unitTypeService.getAllUnitTypes()
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
      this.unitTypeService.getUnitTypeInstanceVariables(unitType)
    ).subscribe(data => {
      let datasets: Dataset[] = data[0]
      let instanceVariables: InstanceVariable[] = data[1]

      let translatedLabel: string = this.langPipe.transform(unitType.prefLabel)
      let translationParams: {} = {
        unitType: translatedLabel,
        datasetCount: datasets.length,
        instanceVariableCount: instanceVariables.length
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
    this.unitTypeService.searchUnitTypes(literalSearchTerms).subscribe(unitTypes => {
      this.unitTypes = unitTypes
      this.loadingUnitTypes = false
    })
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.debounceTime(this.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.loadingUnitTypes = true;
        return this.unitTypeService.searchUnitTypes(term)
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