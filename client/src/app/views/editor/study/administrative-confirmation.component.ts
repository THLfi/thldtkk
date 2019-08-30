import { Component, Input, Output, EventEmitter } from '@angular/core'

import { StudyFormConfirmationState } from '../../../model2/study-form-confirmation-state';

@Component({
  selector: 'administrative-confirmation',
  templateUrl: './administrative-confirmation.component.html',
  styles: [`
  .radio {
    margin: 0;
    padding-top: 4px;
  }
  `]
  })
export class AdministrativeConfirmationComponent {

  @Input() confirmationState: StudyFormConfirmationState;
  @Input() isUserAuthorized: boolean;
  @Input() confirmationName: string;

  @Output() confirmationStateChange = new EventEmitter<StudyFormConfirmationState>();

  StudyFormConfirmationState = StudyFormConfirmationState;

  constructor() { }

  onStateChange(confirmationState: StudyFormConfirmationState) {
    this.confirmationState = confirmationState;
    this.confirmationStateChange.emit(confirmationState);
  }
}
