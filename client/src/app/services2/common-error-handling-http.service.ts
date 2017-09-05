import { Injectable, Injector } from '@angular/core'
import {
  Http, XHRBackend, RequestOptions, Request,
  RequestOptionsArgs, Response
} from '@angular/http'
import { Observable } from 'rxjs'

import { GrowlMessageService } from './growl-message.service'
import { HttpMessageHelper } from '../utils/http-message-helper'

@Injectable()
export class CommonErrorHandlingHttpService extends Http {

  private growlMessageService: GrowlMessageService

  constructor(
    private backend: XHRBackend,
    private defaultOptions: RequestOptions,
    private injector: Injector
  ) {
    super(backend, defaultOptions)
    // Delayed injection required because of cyclic dependency of Http component
    setTimeout(() => this.growlMessageService = injector.get(GrowlMessageService))
  }

  request(url: string | Request, options?: RequestOptionsArgs): Observable<Response> {
    return super.request(url, options).catch((error: Response) => {
      this.growlMessageService.buildAndShowMessage('error', 'errors.server.operationFailed',
        HttpMessageHelper.getErrorMessageByStatusCode(error.status))
      return Observable.throw(error)
    })
  }

}
