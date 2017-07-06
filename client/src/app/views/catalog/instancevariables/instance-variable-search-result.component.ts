import { ActivatedRoute } from '@angular/router'
import { Component, Input} from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { Dataset } from '../../../model2/dataset'
import { DatasetService } from '../../../services2/dataset.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableService } from '../../../services2/instance-variable.service'
import { LangPipe } from '../../../utils/lang.pipe';

@Component({
  templateUrl: './instance-variable-search-result.component.html',
  selector: 'instance-variable-search-result',
  styleUrls: ['./instance-variable-search-result.component.css']
})
export class InstanceVariableSearchResultComponent {

  language: string

  @Input() instanceVariable: InstanceVariable

  constructor(private route: ActivatedRoute,
              private translateService: TranslateService) {
    this.language = this.translateService.currentLang
  }
}
