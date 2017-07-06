import { ActivatedRoute } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { Dataset } from '../../../model2/dataset'
import { DatasetService } from '../../../services2/dataset.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableService } from '../../../services2/instance-variable.service'
import { LangPipe } from '../../../utils/lang.pipe';

@Component({
  templateUrl: './instance-variable-search.component.html',
    styleUrls: ['./instance-variable-search.component.css']
})
export class InstanceVariableSearchComponent implements OnInit {

  language: string
  instanceVariables: InstanceVariable[]
  searchText: string

  constructor(private instanceVariableService: InstanceVariableService,
              private route: ActivatedRoute,
              private translateService: TranslateService) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
  }

  searchInstanceVariables(searchText: string) {
        this.instanceVariableService.searchInstanceVariable(searchText)
          .subscribe(instanceVariables => this.instanceVariables = instanceVariables)
  }

}
