import {
  Component, Output, EventEmitter,
  HostListener, ViewChild, ElementRef
} from '@angular/core'

import { DatasetRelationType } from './dataset-relation-type'

@Component({
  selector: 'dataset-relation-type-dropdown',
  template: `
<div [ngClass]="{ 'dropdown': true, 'open': dropdownOpen }">
  <button #dropdownToggle
          (click)="toggleDropdownVisibility($event)"
          type="button"
          class="btn btn-default dropdown-toggle"
          aria-haspopup="true"
          [attr.aria-expanded]="dropdownOpen">
    <span class="glyphicon glyphicon-plus"></span>
    {{ 'addNewDatasetRelation' | translate }}
    <i class="fa fa-caret-down" aria-hidden="true"></i>
  </button>
  <ul class="dropdown-menu">
    <li><a (click)="selectPredecessor()">{{ 'datasetRelations.predecessor' | translate }}</a></li>
  </ul>
</div>
`
})
export class DatasetRelationTypeDropdown {

  @Output() onSelectType = new EventEmitter<DatasetRelationType>()

  @ViewChild('dropdownToggle') dropdownToggle: ElementRef

  dropdownOpen: boolean = false

  toggleDropdownVisibility(event: any): void {
    event.preventDefault()
    this.dropdownOpen = !this.dropdownOpen
  }

  selectPredecessor() {
    this.onSelectType.emit(DatasetRelationType.PREDECESSOR)
  }

  @HostListener('document:click', ['$event'])
  @HostListener('document:touchstart', ['$event']) // for iOS
  handleOutsideClick(event): void {
    if (this.dropdownOpen && !this.dropdownToggle.nativeElement.contains(event.target)) {
      this.dropdownOpen = false
    }
  }

}
