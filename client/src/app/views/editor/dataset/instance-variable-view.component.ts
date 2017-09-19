import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { Title } from '@angular/platform-browser'
import { TranslateService } from '@ngx-translate/core'

import { Dataset } from '../../../model2/dataset'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { LangPipe } from '../../../utils/lang.pipe'
import { SidebarActiveSection } from './sidebar/sidebar-active-section'

@Component({
  templateUrl: './instance-variable-view.component.html'
})
export class InstanceVariableViewComponent implements OnInit {

  instanceVariable: InstanceVariable
  dataset: Dataset
  instanceVariableId: string
  datasetId: string
  language: string

  sidebarActiveSection = SidebarActiveSection.INSTANCE_VARIABLES

  constructor(private instanceVariableService: EditorInstanceVariableService,
              private datasetService: EditorDatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.datasetId = this.route.snapshot.params['datasetId']
    this.instanceVariableId = this.route.snapshot.params['instanceVariableId']
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getInstanceVariable()
  }

  private getInstanceVariable() {
    Observable.forkJoin(
      this.instanceVariableService.getInstanceVariable(this.datasetId, this.instanceVariableId),
      this.datasetService.getDataset(this.datasetId)
    ).subscribe(data => {
      this.instanceVariable = data[0]
      this.dataset = data[1]
      this.updatePageTitle();
    })
  }

  updatePageTitle():void {
    if(this.instanceVariable.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.instanceVariable.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
