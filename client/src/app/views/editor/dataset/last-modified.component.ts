import { OnInit, Component, Input } from '@angular/core'

import { UserInformation} from '../../../model2/user-information'

@Component({
  selector: 'last-modified',
  templateUrl: './last-modified.component.html'
})
export class LastModifiedComponent {

  @Input() lastModifiedDate: Date
  @Input() modifier: UserInformation

}
