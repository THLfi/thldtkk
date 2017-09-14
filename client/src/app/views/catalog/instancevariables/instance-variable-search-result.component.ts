import { Component, Input} from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { InstanceVariable } from '../../../model2/instance-variable'

@Component({
  templateUrl: './instance-variable-search-result.component.html',
  selector: 'instance-variable-search-result',
  styleUrls: ['./instance-variable-search-result.component.css']
})
export class InstanceVariableSearchResultComponent {

  language: string

  @Input() instanceVariable: InstanceVariable

  constructor(
    private translateService: TranslateService
  ) {
    this.language = this.translateService.currentLang
  }
}
