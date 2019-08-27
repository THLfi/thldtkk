import { Component, Input, Output, EventEmitter } from '@angular/core'
import {LangPipe} from '../../../utils/lang.pipe'

@Component({
  selector: 'administrative-confirmation',
  templateUrl: './administrative-confirmation.component.html',
  styles: [`
  .accept-buttons {
    display: flex;
    flex-direction: column;
    padding: 6px 0 0 0;
  }
  .accept-buttons button + button { margin-top: 6px; }
  `]
  })
export class AdministrativeConfirmationComponent {

  @Input() pendingAcceptance: boolean;
  @Input() isAccepted: boolean;
  @Input() isUserAuthorized: boolean;

  @Output() onConfirmation = new EventEmitter<boolean>();
  @Output() onCancel = new EventEmitter();

  constructor() { }

  respond(response: boolean) {
    this.onConfirmation.emit(response);
  }

  cancelConfirmation() {
    this.onCancel.emit();
  }
}
