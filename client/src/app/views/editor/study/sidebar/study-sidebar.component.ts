import { Component, Input } from '@angular/core'
import { Study } from '../../../../model2/study'
import { StudySidebarActiveSection } from './study-sidebar-active-section'

@Component({
  templateUrl: './study-sidebar.component.html',
  selector: 'study-sidebar'
})

export class StudySidebarComponent {

  @Input() study: Study
  @Input() activeSection: StudySidebarActiveSection

  StudySidebarActiveSection = StudySidebarActiveSection

}