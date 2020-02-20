import {of as observableOf, forkJoin as observableForkJoin,  Observable } from 'rxjs';
import { Injectable } from '@angular/core'
import { Message } from 'primeng/primeng'
import { TranslateService } from '@ngx-translate/core'

@Injectable()
export class GrowlMessageService {

  messages: Message[] = []

  constructor(
    private translateService: TranslateService
  ) { }

  showCommonSaveFailedMessage() {
    this.buildAndShowMessage(
      'error',
      'operations.common.save.result.fail.summary',
      'operations.common.save.result.fail.detail'
    )
  }

  /**
   * @param severity Possible values are 'success', 'info', 'warn' or 'error'. See http://primefaces.org/primeng/#/growl for details.
   * @param summaryKey Message key for growl message summary.
   * @param detailKey Message key for growl message details. Optional.
   */
  buildAndShowMessage(severity: 'success' | 'info' | 'warn' | 'error', summaryKey: string, detailKey?: string) {
    observableForkJoin([
      this.translateService.get(summaryKey),
      detailKey ? this.translateService.get(detailKey) : observableOf('')
    ]).subscribe(data => {
      this.showMessage({
        severity: severity,
        summary: data[0] || summaryKey,
        detail: data[1] || detailKey,
      })
    })
  }

  /**
   * @param message See <a href="http://primefaces.org/primeng/#/growl">PrimeNG - Growl</a> for details.
   */
  showMessage(message: Message) {
    this.messages = [...this.messages, message]
  }

}
