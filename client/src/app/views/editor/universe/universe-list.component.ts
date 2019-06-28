
import {of as observableOf, forkJoin as observableForkJoin,  Observable, Subject } from 'rxjs';

import {catchError, switchMap, distinctUntilChanged, debounceTime} from 'rxjs/operators';
import { Component, OnInit } from '@angular/core';
import { ConfirmationService } from 'primeng/primeng'
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from '../../../services-common/growl-message.service'

import { Dataset } from '../../../model2/dataset'
import { LangPipe } from '../../../utils/lang.pipe'
import { UniverseService } from '../../../services-common/universe.service'
import { Universe } from '../../../model2/universe'








@Component({
  templateUrl: './universe-list.component.html',
  styleUrls: ['./universe-list.component.css']
})
export class UniverseListComponent implements OnInit {

  universes: Universe[] = []
  editUniverse: Universe

  searchTerms: Subject<string>
  searchInput: string

  loadingUniverses: boolean

  readonly searchDelay = 300;
  readonly universeRemoveConfirmationKey: string = 'confirmRemoveUniverseModal.message'
  readonly universeSaveSuccessKey: string = 'operations.common.save.result.success'

  constructor(
    private universeService: UniverseService,
    private confirmationService: ConfirmationService,
    private translateService: TranslateService,
    private langPipe: LangPipe,
    private growlMessageService: GrowlMessageService
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit(): void {
    this.loadingUniverses = true
    this.universeService.getAll()
      .subscribe(universes => {
        this.universes = universes
        this.loadingUniverses = false
      })
    this.initSearchSubscription(this.searchTerms)
  }

  showEditUniverseModal(universe: Universe) {
    this.editUniverse = universe
  }

  closeEditUniverseModal() {
    this.editUniverse = null
    this.refreshUniverses()
  }

  saveUniverse(universe: Universe) {
    this.universeService.save(universe).subscribe(() => {
      this.editUniverse = null
      this.refreshUniverses()
      this.growlMessageService.buildAndShowMessage('success', this.universeSaveSuccessKey)
    })
  }

  showAddNewUniverseModal(): void {
    this.editUniverse = this.universeService.initNew()
  }

  confirmRemoveUniverse(universe: Universe): void {
    observableForkJoin(
      this.universeService.getUniverseDatasets(universe),
      this.universeService.getUniverseStudies(universe),
    ).subscribe(data => {

      let translatedLabel: string = this.langPipe.transform(universe.prefLabel)
      let translationParams: {} = {
        universe: translatedLabel,
        datasetCount: data[0].length,
        publishedDatasetCount: data[0].filter(dataset => dataset.published).length,
        studyCount: data[1].length,
        publishedStudyCount: data[1].filter(study => study.published).length
      }

      this.translateService.get(this.universeRemoveConfirmationKey, translationParams).subscribe(confirmationMessage => {
        this.confirmationService.confirm({
          message: confirmationMessage,
          accept: () => {
            this.universeService.delete(universe.id).subscribe(() => this.refreshUniverses())
          }
        })
      })
    })
  }

  refreshUniverses(): void {
    this.instantSearchUniverses(this.searchInput)
  }

  searchUniverses(literalSearchTerms: string): void {
    this.searchTerms.next(literalSearchTerms)
  }

  instantSearchUniverses(literalSearchTerms: string): void {
    this.loadingUniverses = true
    this.universeService.search(literalSearchTerms).subscribe(universes => {
      this.universes = universes
      this.loadingUniverses = false
    })
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.pipe(debounceTime(this.searchDelay),
      distinctUntilChanged(),
      switchMap(term => {
        this.loadingUniverses = true;
        return this.universeService.search(term)
      }),
      catchError(error => {
        this.initSearchSubscription(searchTerms)
        return observableOf<Universe[]>([])
      }),)
      .subscribe(universes => {
        this.universes = universes
        this.loadingUniverses = false
      })
  }

}
