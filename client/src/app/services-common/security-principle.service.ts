import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env } from '../../environments/environment'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'
import {SupplementaryDigitalSecurityPrinciple} from "../model2/supplementary-digital-security-principle";
import {SupplementaryPhysicalSecurityPrinciple} from "../model2/supplementary-physical-security-principle";

import { GrowlMessageService } from './growl-message.service'
import { NodeUtils } from '../utils/node-utils'

type SecurityPrinciple = SupplementaryPhysicalSecurityPrinciple | SupplementaryDigitalSecurityPrinciple;

@Injectable()
export class SecurityPrincipleService {

  constructor(
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private http: Http) {}

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
    return this.http.get(env.contextPath + env.apiPath + '/supplementaryPhysicalSecurityPrinciples')
      .map(response => response.json() as SupplementaryPhysicalSecurityPrinciple[])
  }

  getAllSupplementaryDigitalSecurityPrinciples(): Observable<SupplementaryDigitalSecurityPrinciple[]> {
    return this.http.get(env.contextPath + env.apiPath + '/supplementaryDigitalSecurityPrinciples')
      .map(response => response.json() as SupplementaryDigitalSecurityPrinciple[])
  }

  getSupplementaryPhysicalSecurityPrinciple(id: string): Observable<SupplementaryPhysicalSecurityPrinciple> {
    return this.http.get(env.contextPath + env.apiPath + '/supplementaryPhysicalSecurityPrinciples/' + id)
      .map(response => response.json() as SupplementaryPhysicalSecurityPrinciple)
  }

  getSupplementaryDigitalSecurityPrinciple(id: string): Observable<SupplementaryDigitalSecurityPrinciple> {
    return this.http.get(env.contextPath + env.apiPath + '/supplementaryDigitalSecurityPrinciples/' + id)
      .map(response => response.json() as SupplementaryDigitalSecurityPrinciple)
  }

  saveSupplementarySecurityPrinciple(principle: SecurityPrinciple): Observable<SecurityPrinciple> {
    return this.saveSupplementarySecurityPrincipleInternal(principle)
      .do(dataset => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.principle.save.result.success')
      })
  }

  private saveSupplementarySecurityPrincipleInternal(principle: SecurityPrinciple): Observable<SecurityPrinciple> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    let principleEndPointFragment: string

    if (principle instanceof SupplementaryPhysicalSecurityPrinciple) {
      principleEndPointFragment = 'supplementaryPhysicalSecurityPrinciples'
    }
    else if (principle instanceof SupplementaryDigitalSecurityPrinciple) {
      principleEndPointFragment = 'supplementaryDigitalSecurityPrinciples'
    }

    return this.http.post(env.contextPath + env.apiPath + '/' + principleEndPointFragment, principle, options)
      .map(response => response.json() as SecurityPrinciple)
  }
}
