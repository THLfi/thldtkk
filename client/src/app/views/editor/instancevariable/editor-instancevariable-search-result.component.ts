import { Component, Input} from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { InstanceVariable } from '../../../model2/instance-variable'

@Component({
    templateUrl: './editor-instancevariable-search-result.component.html',
    selector: 'editor-instancevariable-search-result',
    styleUrls: ['./editor-instancevariable-search-result.component.css']
})
export class EditorInstanceVariableSearchResultComponent {

    language: string

    @Input() instanceVariable: InstanceVariable

    constructor(
        private translateService: TranslateService
    ) {
        this.language = this.translateService.currentLang
    }
}
