import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'
import { LangPipe } from '../../../utils/lang.pipe'

import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'

@Component({
  templateUrl: './instance-variable.component.html',
  styleUrls: [ './instance-variable.component.css' ]
})
export class InstanceVariableComponent implements OnInit {

  instanceVariable: InstanceVariable
  dataset: Dataset
  instanceVariableId: string
  datasetId: string
  language: string

  constructor(private instanceVariableService: PublicInstanceVariableService,
              private datasetService: PublicDatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private langPipe: LangPipe,
              private titleService: Title) {
    this.datasetId = this.route.snapshot.params['datasetId']
    this.instanceVariableId = this.route.snapshot.params['instanceVariableId']
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getInstanceVariable()
  }

  private getInstanceVariable() {
    Observable.forkJoin(
      this.instanceVariableService.get(this.datasetId, this.instanceVariableId),
      this.datasetService.get(this.datasetId)
    ).subscribe(data => {
      this.instanceVariable = data[0]
      this.dataset = data[1]
      this.updatePageTitle()
    })
  }

  private updatePageTitle():void {
    if(this.instanceVariable.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.instanceVariable.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
