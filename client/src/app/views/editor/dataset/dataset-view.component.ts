import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { Title } from '@angular/platform-browser'
import { LangPipe } from '../../../utils/lang.pipe'

import { Dataset } from "../../../model2/dataset";
import { Study } from "../../../model2/study";
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { EditorStudyService } from '../../../services-editor/editor-study.service'
import { StudySidebarActiveSection } from '../study/sidebar/study-sidebar-active-section'

@Component({
  templateUrl: './dataset-view.component.html',
  styleUrls: [ './dataset-view.component.css' ]
})
export class DatasetViewComponent implements OnInit {

  study: Study
  dataset: Dataset
  language: string

  sidebarActiveSection = StudySidebarActiveSection.DATASETS_AND_VARIABLES

  constructor(private datasetService: EditorDatasetService,
              private editorStudyService: EditorStudyService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.dataset = null
      this.getStudy(params['studyId'])
      this.getDataset(params['datasetId'])
    })
  }

  private getDataset(datasetId: string) {
    this.datasetService.getDataset(datasetId)
      .subscribe(dataset => {
        this.dataset = dataset
        this.updatePageTitle()
      })
  }

  private getStudy(studyId: string) {
    this.editorStudyService.getStudy(studyId).subscribe(study => this.study = study)
  }

  updatePageTitle():void {
    if(this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

  confirmPublish(): void {
    this.translateService.get('confirmPublishDataset')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.datasetService.publish(this.dataset)
            .subscribe(dataSet => this.dataset = dataSet)
        }
      })
  }

  confirmWithdraw(): void {
    this.translateService.get('confirmWithdrawDataset')
      .subscribe((message: string) => {
        if (confirm(message)) {
          this.datasetService.withdraw(this.dataset)
            .subscribe(dataSet => this.dataset = dataSet)
        }
      })
  }

}
