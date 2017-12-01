import { ActivatedRoute } from '@angular/router'
import { Component } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { PublicStudyService } from '../../../services-public/public-study.service'
import { LangPipe  } from '../../../utils/lang.pipe'
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

  Math: Math
  readonly datasetLabelTruncateLength: number = 50
  readonly datasetLabelTruncateFromEndLength: number = -20

  constructor(
    private studyService: PublicStudyService,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private titleService: Title,
    private langPipe: LangPipe) {
      this.language = this.translateService.currentLang
      this.Math = Math
    }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadingStudy = true
      this.study = null

      this.studyService.getStudy(params['id']).subscribe(study => {
        this.study = study
        this.updatePageTitle()
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
