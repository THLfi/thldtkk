import {
  Component, Output, EventEmitter,
  HostListener, ViewChild, ElementRef
} from '@angular/core'

import { StudyRelationType } from './study-relation-type'

@Component({
  selector: 'study-relation-type-dropdown',
  template: `
<div [ngClass]="{ 'dropdown': true, 'open': dropdownOpen }">
  <button #dropdownToggle
          (click)="toggleDropdownVisibility($event)"
          type="button"
          class="btn btn-default dropdown-toggle"
          aria-haspopup="true"
          [attr.aria-expanded]="dropdownOpen">
    <glyphicon icon="plus"></glyphicon>
    {{ 'study.relations.addNew' | translate }}
    <fa icon="caret-down"></fa>
  </button>
  <ul class="dropdown-menu">
    <li><a (click)="selectPredecessor()">{{ 'study.relations.predecessor' | translate }}</a></li>
  </ul>
</div>
`
})
export class StudyRelationTypeDropdown {

  @Output() onSelectType = new EventEmitter<StudyRelationType>()

  @ViewChild('dropdownToggle') dropdownToggle: ElementRef

  dropdownOpen: boolean = false

  toggleDropdownVisibility(event: any): void {
    event.preventDefault()
    this.dropdownOpen = !this.dropdownOpen
  }

  selectPredecessor() {
    this.onSelectType.emit(StudyRelationType.PREDECESSOR)
  }

  @HostListener('document:click', ['$event'])
  @HostListener('document:touchstart', ['$event']) // for iOS
  handleOutsideClick(event): void {
    if (this.dropdownOpen && !this.dropdownToggle.nativeElement.contains(event.target)) {
      this.dropdownOpen = false
    }
  }

}
