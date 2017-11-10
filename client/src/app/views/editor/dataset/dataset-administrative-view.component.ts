import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { Title } from '@angular/platform-browser'
import { LangPipe } from '../../../utils/lang.pipe'

import { Dataset } from "../../../model2/dataset";
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { SidebarActiveSection } from './sidebar/sidebar-active-section'

@Component({
  templateUrl: './dataset-administrative-view.component.html',
  styleUrls: [ './dataset-administrative-view.component.css' ]
})
export class DatasetAdministrativeViewComponent implements OnInit {

  dataset: Dataset
  language: string

  sidebarActiveSection = SidebarActiveSection.ADMINISTRATIVE_INFORMATION

  constructor(private datasetService: EditorDatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getDataSet();
  }

  getDataSet() {
    const studyId = this.route.snapshot.params['studyId']
    const datasetId = this.route.snapshot.params['datasetId']
    this.datasetService.getDataset(studyId, datasetId)
      .subscribe(dataset => {
        this.dataset = dataset
        this.updatePageTitle()
      })
  }

  updatePageTitle():void {
    if(this.dataset.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.dataset.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }


}
