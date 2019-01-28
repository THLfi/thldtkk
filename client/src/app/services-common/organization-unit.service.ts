import { Injectable } from '@angular/core'
import { Headers, Http, RequestOptions } from '@angular/http'
import { LangPipe } from '../utils/lang.pipe'
import { Observable } from 'rxjs'
import { NodeUtils } from '../utils/node-utils'
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env } from '../../environments/environment'

import { Dataset } from '../model2/dataset'
import { GrowlMessageService } from './growl-message.service'
import { OrganizationUnit } from '../model2/organization-unit'
import { TranslateService } from '@ngx-translate/core'
import { Study } from '../model2/study'

@Injectable()
export class OrganizationUnitService {

  private language: string

  constructor(
    private http: Http,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private langPipe: LangPipe,
    private translateService: TranslateService
  ) {
    this.language = translateService.currentLang
  }

  save(organizationUnit: OrganizationUnit): Observable<OrganizationUnit> {
    const path: string = env.contextPath + env.apiPath + '/organizationUnits/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, organizationUnit, options)
      .map(response => response.json() as OrganizationUnit)
      .do(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.organizationUnit.save.result.success')
      })
  }

  delete(organizationUnitId: string): Observable<any> {
    const path: string = env.contextPath + env.apiPath + '/organizationUnits/' + organizationUnitId

    return this.http.delete(path)
      .map(response => response.json())
      .do(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.organizationUnit.delete.result.success')
      })
  }

  initNew(): OrganizationUnit {
    const newOrganizationUnit = {
      id: null,
      parentOrganizationId: null,
      prefLabel: null,
      abbreviation: null
    }

    this.initProperties(newOrganizationUnit, ['prefLabel', 'abbreviation'])

    return newOrganizationUnit
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.language ])
  }

  getOrganizationUnitStudies(organizationUnit: OrganizationUnit): Observable<Study[]> {
    const path: string = env.contextPath
      + env.apiPath
      + '/organizationUnits/'
      + organizationUnit.id
      + '/studies'

    return this.http.get(path)
      .map(response => response.json() as Study[])
  }

  getOrganizationUnitDatasets(organizationUnit: OrganizationUnit): Observable<Dataset[]> {
    const path: string = env.contextPath
      + env.apiPath
      + '/organizationUnits/'
      + organizationUnit.id
      + '/datasets'

    return this.http.get(path)
      .map(response => response.json() as Dataset[])
  }

}
