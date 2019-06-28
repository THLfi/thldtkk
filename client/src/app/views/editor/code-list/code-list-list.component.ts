
import {of as observableOf,  Observable, Subject } from 'rxjs';

import {catchError, switchMap, distinctUntilChanged, debounceTime} from 'rxjs/operators';
import { Component, OnInit } from '@angular/core';
import { ConfirmationService } from 'primeng/primeng'
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from '../../../services-common/growl-message.service'

import { Dataset } from '../../../model2/dataset'
import { LangPipe } from '../../../utils/lang.pipe'
import { CodeListService3 } from '../../../services-common/code-list.service'
import { CodeList } from '../../../model2/code-list'








@Component({
  templateUrl: './code-list-list.component.html',
  styleUrls: ['./code-list-list.component.css']
})
export class CodeListListComponent implements OnInit {

  codeLists: CodeList[] = []
  editCodeList: CodeList

  searchTerms: Subject<string>
  searchInput: string

  loadingCodeLists: boolean

  readonly searchDelay = 300;
  readonly codeListRemoveConfirmationKey: string = 'confirmRemoveCodeListModal.message'
  readonly codeListSaveSuccessKey: string = 'operations.common.save.result.success'

  constructor(
    private codeListService: CodeListService3,
    private confirmationService: ConfirmationService,
    private translateService: TranslateService,
    private langPipe: LangPipe,
    private growlMessageService: GrowlMessageService
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit(): void {
    this.loadingCodeLists = true
    this.codeListService.getAll()
      .subscribe(codeLists => {
        this.codeLists = codeLists
        this.loadingCodeLists = false
      })
    this.initSearchSubscription(this.searchTerms)
  }

  showEditCodeListModal(codeList: CodeList) {
    this.editCodeList = codeList
  }

  closeEditCodeListModal() {
    this.editCodeList = null
    this.refreshCodeLists()
  }

  saveCodeList(codeList: CodeList) {
    this.codeListService.save(codeList).subscribe(() => {
      this.editCodeList = null
      this.refreshCodeLists()
    })
  }

  showAddNewCodeListModal(): void {
    this.editCodeList = this.codeListService.initNew()
  }

  confirmRemoveCodeList(codeList: CodeList): void {
    this.codeListService.getCodeListInstanceVariables(codeList.id)
        .subscribe(instanceVariables => {

      let translatedLabel: string = this.langPipe.transform(codeList.prefLabel)
      let translationParams: {} = {
        codeList: translatedLabel,
        instanceVariableCount: instanceVariables.length,
        publishedInstanceVariableCount: instanceVariables.filter(variable => variable.published).length
      }

      this.translateService.get(this.codeListRemoveConfirmationKey, translationParams).subscribe(confirmationMessage => {
        this.confirmationService.confirm({
          message: confirmationMessage,
          accept: () => {
            this.codeListService.delete(codeList.id).subscribe(() => this.refreshCodeLists())
          }
        })
      })
    })
  }

  refreshCodeLists(): void {
    this.instantSearchCodeLists(this.searchInput)
  }

  searchCodeLists(literalSearchTerms: string): void {
    this.searchTerms.next(literalSearchTerms)
  }

  instantSearchCodeLists(literalSearchTerms: string): void {
    this.loadingCodeLists = true
    this.codeListService.search(literalSearchTerms).subscribe(codeLists => {
      this.codeLists = codeLists
      this.loadingCodeLists = false
    })
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.pipe(debounceTime(this.searchDelay),
      distinctUntilChanged(),
      switchMap(term => {
        this.loadingCodeLists = true;
        return this.codeListService.search(term)
      }),
      catchError(error => {
        this.initSearchSubscription(searchTerms)
        return observableOf<CodeList[]>([])
      }),)
      .subscribe(codeLists => {
        this.codeLists = codeLists
        this.loadingCodeLists = false
      })
  }

}
