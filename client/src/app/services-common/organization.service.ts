import { Injectable } from '@angular/core'
import { Headers, Http, RequestOptions} from '@angular/http'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { environment as env } from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { LangPipe } from '../utils/lang.pipe'
import { Organization } from '../model2/organization'
import { NodeUtils } from '../utils/node-utils'
import { TranslateService } from '@ngx-translate/core'


@Injectable()
export class OrganizationService {

  private language: string

  constructor(
    private langPipe: LangPipe,
    private nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private http: Http,
    private translateService: TranslateService
  ) {
    this.language = this.translateService.currentLang
  }

  getAllOrganizations(): Observable<Organization[]> {
    return this.http.get(env.contextPath + env.apiPath + '/organizations')
      .map(response => response.json() as Organization[])
      .do(organizations => {
        organizations.forEach(organization => {
          organization.organizationUnit.sort((one, two) => {
            const onePrefLabel = this.langPipe.transform(one.prefLabel)
            const twoPrefLabel = this.langPipe.transform(two.prefLabel)
            return onePrefLabel.localeCompare(twoPrefLabel)
          })
        })
      })
  }

  get(id: string): Observable<Organization> {
    return this.http.get(env.contextPath + env.apiPath + '/organizations/' + id)
      .map(response => response.json() as Organization)
  }

  save(organization: Organization): Observable<Organization> {
    const path: string = env.contextPath + env.apiPath + '/organizations/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, organization, options)
      .map(response => response.json() as Organization)
      .do(organization => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.organization.save.result.success')
      })
  }

  initNew(): Organization {
    const newOrganization = {
      id: null,
      parentOrganizationId: null,
      prefLabel: null,
      abbreviation: null,
      organizationUnit: [],
      phoneNumberForRegistryPolicy: null,
      addressForRegistryPolicy: null
    }

    this.initProperties(newOrganization, ['prefLabel', 'abbreviation', 'addressForRegistryPolicy'])

    return newOrganization
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.language ])
  }

}
