
import {throwError as observableThrowError,  Observable } from 'rxjs';
import { Injectable } from "@angular/core";
import { HttpInterceptor, HttpErrorResponse, HttpRequest, HttpHandler, HttpEvent } from "@angular/common/http";
import { GrowlMessageService } from "app/services-common/growl-message.service";
import { HttpMessageHelper } from "app/utils/http-message-helper";
import { tap, catchError } from 'rxjs/operators';

@Injectable()
export class RequestErrorInterceptor implements HttpInterceptor {

    constructor(
        private growlMessageService: GrowlMessageService
    ) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            // tap((evt) => {}),
            catchError((error: any) => {
                if (error instanceof HttpErrorResponse) {
                    if (error.url.indexOf('login') > -1) {
                        // Error on login page → will be handled separately → skip common error handling
                        return observableThrowError(error)
                    }
                    else if (error.status === 401) {
                        window.location.href = '/login?timeout=true';
                    }

                    this.growlMessageService.buildAndShowMessage('error', 'errors.server.operationFailed',
                        HttpMessageHelper.getErrorMessageByStatusCode(error.status))

                    return observableThrowError(error);
                }
            })
        )
    }
}