import { ActivatedRoute } from '@angular/router'
import { Component } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { BreadcrumbService } from '../../../services-common/breadcrumb.service'
import { LangPipe  } from '../../../utils/lang.pipe'
import { PublicStudyService } from '../../../services-public/public-study.service'
import { Study } from '../../../model2/study'
import { Title } from '@angular/platform-browser'

@Component({
  templateUrl:'./catalog-study-view.component.html',
  styleUrls: ['./catalog-study-view.component.css']
})
export class CatalogStudyViewComponent {

  study: Study
  loadingStudy: boolean
  language: string

  constructor(
    private studyService: PublicStudyService,
    private breadcrumbService: BreadcrumbService,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title,
    private langPipe: LangPipe) {
      this.language = this.translateService.currentLang
    }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadingStudy = true
      this.study = null

      this.studyService.getStudy(params['id']).subscribe(study => {
        this.study = study
        this.updatePageTitle()
        this.breadcrumbService.updateCatalogBreadcrumbsForStudyDatasetAndInstanceVariable(study)
        this.loadingStudy = false
      })
    })
  }

  private updatePageTitle():void {
    if(this.study.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.study.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
