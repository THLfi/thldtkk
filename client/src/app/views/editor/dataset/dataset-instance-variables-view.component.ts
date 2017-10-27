import { OnInit, Component } from '@angular/core'
import { EditorDatasetService } from '../../../services-editor/editor-dataset.service'
import { EditorInstanceVariableService } from '../../../services-editor/editor-instance-variable.service'
import { ActivatedRoute } from '@angular/router'
import { TranslateService } from '@ngx-translate/core'
import { Title } from '@angular/platform-browser'

import { Dataset } from '../../../model2/dataset'
import { LangPipe } from '../../../utils/lang.pipe'
import { SidebarActiveSection } from './sidebar/sidebar-active-section'

@Component({
  templateUrl: './dataset-instance-variables-view.component.html'
})
export class DatasetInstanceVariablesViewComponent implements OnInit {

  dataset: Dataset
  language: string

  datasetForImportInstanceVariablesModal: Dataset

  sidebarActiveSection = SidebarActiveSection.INSTANCE_VARIABLES

  constructor(private datasetService: EditorDatasetService,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private titleService: Title,
              private langPipe: LangPipe,
              private instanceVariableService: EditorInstanceVariableService) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.getDataset();
  }

  getDataset() {
    const datasetId = this.route.snapshot.params['datasetId'];
    this.datasetService.getDataset(datasetId)
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

  showImportInstanceVariablesModal() {
    this.datasetForImportInstanceVariablesModal = this.dataset
  }

  closeImportInstanceVariablesModal(): void {
    this.datasetForImportInstanceVariablesModal = null
  }

  composeInstanceVariableExportUrl(): string {
    return this.instanceVariableService.getInstanceVariableAsCsvExportPath(this.dataset.id)
  }

}
