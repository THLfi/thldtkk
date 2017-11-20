import { ActivatedRoute } from '@angular/router'
import { Component, OnInit} from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { InstanceVariable } from '../../../model2/instance-variable'
import { LangPipe } from '../../../utils/lang.pipe'
import { Title } from '@angular/platform-browser'
import { Variable } from '../../../model2/variable'
import { VariableService } from '../../../services-common/variable.service'

@Component({
  templateUrl: './variable-view.component.html'
})
export class VariableViewComponent implements OnInit {

  language: string

  variable: Variable
  instanceVariables: InstanceVariable[]
  loadingInstanceVariables: boolean

  constructor(private route: ActivatedRoute,
              private translateService: TranslateService,
              private variableService: VariableService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    let variableId:string = this.route.snapshot.params['id']
    this.loadingInstanceVariables = true
    this.variableService.get(variableId).subscribe(variable => {
      this.variable = variable
      this.updatePageTitle()
      });
    this.variableService.getInstanceVariables(variableId)
      .subscribe(instanceVariables => {
        this.instanceVariables = instanceVariables
        this.loadingInstanceVariables = false  
      })
  }

  updatePageTitle() {
    if(this.variable.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.variable.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
