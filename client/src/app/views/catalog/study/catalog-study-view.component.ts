import { ActivatedRoute, Router } from '@angular/router'
import { Component } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { LangPipe } from '../../../utils/lang.pipe'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { Study } from '../../../model2/study'
import { StudyDetailHighlight } from './study-detail-highlight'
import { StringUtils } from '../../../utils/string-utils'
import { Title } from '@angular/platform-browser'
import {forkJoin} from "rxjs";

@Component({
  templateUrl: './catalog-study-view.component.html',
  styleUrls: ['./catalog-study-view.component.css']
})
export class CatalogStudyViewComponent {

  study: Study
  loadingStudy: boolean
  language: string

  Math: Math
  readonly datasetLabelTruncateLength: number = 70

  StudyDetailHighlight = StudyDetailHighlight
  highlights: StudyDetailHighlight[] = [];
  instanceVariableCounts: {[id: string]: number} = {};

  constructor(
    private studyService: PublicStudyService,
    private breadcrumbService: BreadcrumbService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService,
    private titleService: Title,
    private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
    this.Math = Math
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadingStudy = true;
      this.study = null;

      forkJoin([
        this.studyService.getStudyWithSelect(params['id'], [
          'properties.*',
          'references.studyGroup',
          'references.ownerOrganization',
          'references.personInRoles',
          'references.person:2',
          'references.role:2',
          'references.datasetTypes',
          'references.usageCondition',
          'references.links',
          'references.universe',
          'references.referencePeriodStart',
          'references.referencePeriodEnd',
          'references.population',
          'references.conceptsFromScheme',
          'references.dataSets'
        ]),
        this.studyService.getStudyWithSelect(params['id'], [
          'references.dataSets',
          'references.instanceVariable:2'
        ])
      ])
        .subscribe(fork => {
          this.study = fork[0];
          fork[1].datasets
            .forEach(dataset => this.instanceVariableCounts[dataset.id] = dataset.instanceVariables.length);

          this.updatePageTitle()
          this.breadcrumbService.updateCatalogBreadcrumbsForStudyDatasetAndInstanceVariable(this.study)
          this.pickHighlightedDetails()
          this.loadingStudy = false
        }, 
          e => {
            this.router.navigate(['/catalog'])
          })
    })
  }

  private updatePageTitle(): void {
    if (this.study.prefLabel) {
      let translatedLabel: string = this.langPipe.transform(this.study.prefLabel)
      let bareTitle: string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

  pickHighlightedDetails() {
    if (this.study.universe) {
      this.highlights.push(StudyDetailHighlight.UNIVERSE)
    }

    if (this.study.referencePeriodStart || this.study.referencePeriodEnd) {
      this.highlights.push(StudyDetailHighlight.REFERENCE_PERIOD)
    }

    if (this.study.population && this.study.population.geographicalCoverage) {
      let coverageInCurrentLang: string = this.langPipe.transform(this.study.population.geographicalCoverage)
      if (StringUtils.isNotEmpty(coverageInCurrentLang)) {
        this.highlights.push(StudyDetailHighlight.GEOGRAPHICAL_COVERAGE)
      }
    }
  }
}
