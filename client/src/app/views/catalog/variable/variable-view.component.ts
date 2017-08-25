import { ActivatedRoute } from '@angular/router'
import { Component, Input, OnInit} from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableService } from '../../../services2/instance-variable.service'
import { VariableService } from '../../../services2/variable.service'
import { LangPipe } from '../../../utils/lang.pipe';
import { Title } from '@angular/platform-browser'
import { Variable } from '../../../model2/variable'

@Component({
  templateUrl: './variable-view.component.html'
})

export class VariableViewComponent implements OnInit {

  language: string

  variable: Variable
  instanceVariables: InstanceVariable[]

  constructor(private route: ActivatedRoute,
              private translateService: TranslateService,
              private instanceVariableService: InstanceVariableService,
              private variableService: VariableService,
              private titleService: Title,
              private langPipe: LangPipe) {
    this.language = this.translateService.currentLang
  }

  ngOnInit():void {
    let variableId:string = this.route.snapshot.params['id']
    this.variableService.getVariable(variableId).subscribe(variable => {
      this.variable = variable
      this.updatePageTitle()
      });
    this.variableService.getInstanceVariables(variableId).subscribe(instanceVariables => this.instanceVariables = instanceVariables)
  }

  updatePageTitle():void {
    if(this.variable.prefLabel) {
      let translatedLabel:string = this.langPipe.transform(this.variable.prefLabel)
      let bareTitle:string = this.titleService.getTitle();
      this.titleService.setTitle(translatedLabel + " - " + bareTitle)
    }
  }

}
