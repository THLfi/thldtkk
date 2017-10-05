import { Component, Input, Output, EventEmitter } from '@angular/core'

@Component({
  templateUrl: './logout-message.component.html',
  styleUrls: ['./logout-message.component.css'],
  selector: 'logout-message'
})

export class LogoutMessageComponent {

  @Input() message: string
  @Input() messageDetails: string
  
  @Output() onClose: EventEmitter<void> = new EventEmitter<void>()
  
  close():void {
    this.message = ""
    this.messageDetails = ""
    this.onClose.emit()
  }

}
