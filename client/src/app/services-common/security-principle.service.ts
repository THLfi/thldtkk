
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env } from '../../environments/environment'
import { Observable } from 'rxjs'

import {SupplementaryDigitalSecurityPrinciple} from "../model2/supplementary-digital-security-principle";
import {SupplementaryPhysicalSecurityPrinciple} from "../model2/supplementary-physical-security-principle";

import { GrowlMessageService } from './growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { HttpClient } from '@angular/common/http';

type SecurityPrinciple = SupplementaryPhysicalSecurityPrinciple | SupplementaryDigitalSecurityPrinciple;

@Injectable()
export class SecurityPrincipleService {

  constructor(
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private http: HttpClient) {}

  public initNewSupplementaryPhysicalSecurityPrinciple(): SupplementaryPhysicalSecurityPrinciple {
    return this.initializeProperties(new SupplementaryPhysicalSecurityPrinciple())
  }

  public initNewSupplementaryDigitalSecurityPrinciple(): SupplementaryDigitalSecurityPrinciple {
    return this.initializeProperties(new SupplementaryDigitalSecurityPrinciple())
  }

  initializeProperties(principle: SecurityPrinciple): SecurityPrinciple {
    principle.id = null

    this.initProperties(principle, [
      'prefLabel'
    ])

    return principle
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.translateService.currentLang ])
  }

  getAllSupplementaryPhysicalSecurityPrinciples(): Observable<SupplementaryPhysicalSecurityPrinciple[]> {
    return this.http.get<SupplementaryPhysicalSecurityPrinciple[]>(env.contextPath + env.apiPath + '/supplementaryPhysicalSecurityPrinciples');
  }

  getAllSupplementaryDigitalSecurityPrinciples(): Observable<SupplementaryDigitalSecurityPrinciple[]> {
    return this.http.get<SupplementaryDigitalSecurityPrinciple[]>(env.contextPath + env.apiPath + '/supplementaryDigitalSecurityPrinciples');
  }

  getSupplementaryPhysicalSecurityPrinciple(id: string): Observable<SupplementaryPhysicalSecurityPrinciple> {
    return this.http.get<SupplementaryPhysicalSecurityPrinciple>(env.contextPath + env.apiPath + '/supplementaryPhysicalSecurityPrinciples/' + id);
  }

  getSupplementaryDigitalSecurityPrinciple(id: string): Observable<SupplementaryDigitalSecurityPrinciple> {
    return this.http.get<SupplementaryDigitalSecurityPrinciple>(env.contextPath + env.apiPath + '/supplementaryDigitalSecurityPrinciples/' + id);
  }

  saveSupplementarySecurityPrinciple(principle: SecurityPrinciple): Observable<SecurityPrinciple> {
    return this.saveSupplementarySecurityPrincipleInternal(principle).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.principle.save.result.success')
      }))
  }

  private saveSupplementarySecurityPrincipleInternal(principle: SecurityPrinciple): Observable<SecurityPrinciple> {
    let principleEndPointFragment: string

    if (principle instanceof SupplementaryPhysicalSecurityPrinciple) {
      principleEndPointFragment = 'supplementaryPhysicalSecurityPrinciples'
    }
    else if (principle instanceof SupplementaryDigitalSecurityPrinciple) {
      principleEndPointFragment = 'supplementaryDigitalSecurityPrinciples'
    }

    return this.http.post<SecurityPrinciple>(env.contextPath + env.apiPath + '/' + principleEndPointFragment, principle);
  }
}
