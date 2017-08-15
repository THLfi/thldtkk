import { OnInit, Component, Input } from '@angular/core'

import { Dataset } from '../../../../model2/dataset'
import { SidebarActiveSection } from './sidebar-active-section'

@Component({
  selector: 'dataset-sidebar',
  templateUrl: './dataset-sidebar.component.html'
})
export class DatasetSidebarComponent {

  @Input() dataset: Dataset
  @Input() activeSection: SidebarActiveSection

  SidebarActiveSection = SidebarActiveSection

}
