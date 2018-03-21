import { Injectable } from '@angular/core'
import {Headers, Http, RequestOptions} from '@angular/http'
import { Observable } from 'rxjs'
import {NodeUtils} from '../utils/node-utils'
import {LangPipe} from '../utils/lang.pipe'
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'




import { environment as env } from '../../environments/environment'

import { OrganizationUnit } from '../model2/organization-unit'
import {GrowlMessageService} from "./growl-message.service";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class OrganizationUnitService {

  language: string

  constructor(
    private http: Http,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private langPipe: LangPipe,
    private translateService: TranslateService
) {
    this.language = translateService.currentLang
  }

  getAllOrganizationUnits(): Observable<OrganizationUnit[]> {
    return this.http.get(env.contextPath + env.apiPath + '/organizationUnits')
      .map(response => response.json() as OrganizationUnit[])
  }

  save(organizationUnit: OrganizationUnit): Observable<OrganizationUnit> {
    const path: string = env.contextPath + env.apiPath + '/organizationUnits/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, organizationUnit, options)
      .map(response => response.json() as OrganizationUnit)
      .do(organizationUnit => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.organization_unit.save.result.success')
      })
  }

  initNew(): OrganizationUnit {
    var newOrganizationUnit = {
      id: null,
      parentOrganizationId: null,
      prefLabel: null,
      abbreviation: null
    }

    this.initProperties(newOrganizationUnit, ['prefLabel', 'abbreviation'])

    return newOrganizationUnit;
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.language ])
  }

}
