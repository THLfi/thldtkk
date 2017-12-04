import { Injectable } from '@angular/core'
import { Observable, BehaviorSubject } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { Dataset } from '../model2/dataset'
import { InstanceVariable } from '../model2/instance-variable'
import { Study } from '../model2/study'

export interface Breadcrumb {
  label : string
  url ?: string
}

@Injectable()
export class BreadcrumbService {

  private breadcrumbsSubject: BehaviorSubject<Breadcrumb[]>

  constructor(
    private translateService: TranslateService
  ) {
    this.breadcrumbsSubject = new BehaviorSubject([])
  }

  get currentBreadcrumbs(): Observable<Breadcrumb[]> {
    return this.breadcrumbsSubject.asObservable()
  }

  clearBreadcrumbs(): void {
    this.updateBreadcrumbs([])
  }

  updateBreadcrumbs(breadcrumbs : Breadcrumb[]): void {
    this.breadcrumbsSubject.next(breadcrumbs === null ? [] : breadcrumbs)
  }

  updateCatalogBreadcrumbsForStudyDatasetAndInstanceVariable(
    study ?: Study, dataset ?: Dataset, instanceVariable ?: InstanceVariable): void {
    this.updateBreadcrumbsForStudyDatasetAndInstanceVariable(true, study, dataset, instanceVariable)
  }

  private updateBreadcrumbsForStudyDatasetAndInstanceVariable(
    isCatalog: boolean, study ?: Study, dataset ?: Dataset, instanceVariable ?: InstanceVariable): void {
    const currentLang = this.translateService.currentLang
    const urlPrefix = isCatalog ? '/catalog' : '/editor'

    this.translateService.get('study.studies')
      .subscribe(studiesLabel => {
        let crumbs: Breadcrumb[] = [
          {
            label: studiesLabel,
            url: study ? urlPrefix + '/studies' : null
          }
        ]

        if (study) {
          crumbs = [
            ...crumbs,
            {
              label: study.prefLabel[currentLang],
              url: dataset ? urlPrefix + '/studies/' + study.id : null
            }
          ]
        }
        if (study && dataset) {
          crumbs = [
            ...crumbs,
            {
              label: dataset.prefLabel[currentLang],
              url: instanceVariable ? urlPrefix + '/studies/' + study.id + '/datasets/' + dataset.id : null
            }
          ]
        }
        if (study && dataset && instanceVariable) {
          crumbs = [
            ...crumbs,
            {
              label: instanceVariable.prefLabel[currentLang]
            }
          ]
        }

        this.breadcrumbsSubject.next(crumbs)
      })
  }

  updateEditorBreadcrumbsForStudyDatasetAndInstanceVariable(
    study ?: Study, dataset ?: Dataset, instanceVariable ?: InstanceVariable): void {
    this.updateBreadcrumbsForStudyDatasetAndInstanceVariable(false, study, dataset, instanceVariable)
  }

}
