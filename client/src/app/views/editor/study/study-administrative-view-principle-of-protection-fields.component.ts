import {Component, Input} from '@angular/core'
import {Study} from '../../../model2/study';

@Component({
  selector: 'study-administrative-view-principle-of-protection-fields',
  templateUrl: './study-administrative-view-principle-of-protection-fields.component.html'
})
export class StudyAdministrativeViewPrincipleOfProtectionFieldsComponent {
  @Input()
  study: Study;
}
