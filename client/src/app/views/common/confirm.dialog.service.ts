import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { of } from 'rxjs';
import { ConfirmationService } from 'primeng/primeng'

@Injectable()
export class ConfirmDialogService {
    private theBoolean: BehaviorSubject<boolean>;
    private confirmationService: ConfirmationService;
    constructor() {

    }
  confirm(message?: string): Observable<boolean> {
    const confirmation = window.confirm(message || 'Are you sure?');
    return of(confirmation);
  };
} 