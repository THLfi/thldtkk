import { Component, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ConfirmationService } from 'primeng/primeng'
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from '../../../services-common/growl-message.service'

import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { LangPipe } from '../../../utils/lang.pipe'
import { VariableService } from '../../../services-common/variable.service'
import { Variable } from '../../../model2/variable'

import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import 'rxjs/add/observable/of';

@Component({
  templateUrl: './variable-list.component.html',
  styleUrls: ['./variable-list.component.css']
})
export class VariableListComponent implements OnInit {

  variables: Variable[] = []
  editVariable: Variable

  searchTerms: Subject<string>
  searchInput: string

  loadingVariables: boolean

  readonly searchDelay = 300;
  readonly variableRemoveConfirmationKey: string = 'operations.variable.delete.confirmRemoveVariableModal.message'
  readonly variableSaveSuccessKey: string = 'operations.variable.save.result.success'

  constructor(
    private variableService: VariableService,
    private confirmationService: ConfirmationService,
    private translateService: TranslateService,
    private langPipe: LangPipe,
    private growlMessageService: GrowlMessageService
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit(): void {
    this.loadingVariables = true
    this.variableService.getAll()
      .subscribe(variables => {
        this.variables = variables
        this.loadingVariables = false
      })
    this.initSearchSubscription(this.searchTerms)
  }

  showEditVariableModal(variable: Variable) {
    this.editVariable = variable
  }

  closeEditVariableModal() {
    this.editVariable = null
    this.refreshVariables()
  }

  saveVariable(variable: Variable) {
    this.variableService.save(variable).subscribe(() => {
      this.editVariable = null
      this.refreshVariables()
    })
  }

  showAddNewVariableModal(): void {
    this.editVariable = this.variableService.initNew()
  }

  confirmRemoveVariable(variable: Variable): void {
    this.variableService.getVariableInstanceVariables(variable).subscribe(instanceVariables => {

      let translatedLabel: string = this.langPipe.transform(variable.prefLabel)
      let translationParams: {} = {
        variable: translatedLabel,
        instanceVariableAmount: instanceVariables.length
      }

      this.translateService.get(this.variableRemoveConfirmationKey, translationParams).subscribe(confirmationMessage => {
        this.confirmationService.confirm({
          message: confirmationMessage,
          accept: () => {
            this.variableService.delete(variable.id).subscribe(() => this.refreshVariables())
          }
        })
      })
    })
  }

  refreshVariables(): void {
    this.instantSearchVariables(this.searchInput)
  }

  searchVariables(literalSearchTerms: string): void {
    this.searchTerms.next(literalSearchTerms)
  }

  instantSearchVariables(literalSearchTerms: string): void {
    this.loadingVariables = true
    this.variableService.search(literalSearchTerms).subscribe(variables => {
      this.variables = variables
      this.loadingVariables = false
    })
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.debounceTime(this.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.loadingVariables = true;
        return this.variableService.search(term)
      })
      .catch(error => {
        this.initSearchSubscription(searchTerms)
        return Observable.of<Variable[]>([])
      })
      .subscribe(variables => {
        this.variables = variables
        this.loadingVariables = false
      })
  }

}
