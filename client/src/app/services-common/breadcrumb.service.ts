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

  updateBreadcrumbsForStudyDatasetAndInstanceVariable(
    study ?: Study, dataset ?: Dataset, instanceVariable ?: InstanceVariable): void {
    const currentLang = this.translateService.currentLang

    this.translateService.get('study.studies')
      .subscribe(studiesLabel => {
        let crumbs: Breadcrumb[] = [
          {
            label: studiesLabel,
            url: study ? '/editor/studies' : null
          }
        ]

        if (study) {
          crumbs = [
            ...crumbs,
            {
              label: study.prefLabel[currentLang],
              url: dataset ? '/editor/studies/' + study.id : null
            }
          ]
        }
        if (study && dataset) {
          crumbs = [
            ...crumbs,
            {
              label: dataset.prefLabel[currentLang],
              url: instanceVariable ? '/editor/studies/' + study.id + '/datasets/' + dataset.id : null
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

}
