
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'



import { environment as env } from '../../environments/environment'

import { GrowlMessageService } from './growl-message.service'
import { LangPipe } from '../utils/lang.pipe'
import { Organization } from '../model2/organization'
import { NodeUtils } from '../utils/node-utils'
import { TranslateService } from '@ngx-translate/core'
import { HttpClient } from '@angular/common/http';


@Injectable()
export class OrganizationService {

  private language: string

  constructor(
    private langPipe: LangPipe,
    private nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private http: HttpClient,
    private translateService: TranslateService
  ) {
    this.language = this.translateService.currentLang
  }

  getAllOrganizations(includeReferences = true): Observable<Organization[]> {
    const path = `${env.contextPath}${env.apiPath}/organizations?includeReferences=${includeReferences}`;
    return this.http.get<Organization[]>(path).pipe(
      tap(organizations => {
        organizations.forEach(organization => {
          organization.organizationUnit.sort((one, two) => {
            const onePrefLabel = this.langPipe.transform(one.prefLabel)
            const twoPrefLabel = this.langPipe.transform(two.prefLabel)
            return onePrefLabel.localeCompare(twoPrefLabel)
          })
        })
      }))
  }

  get(id: string): Observable<Organization> {
    return this.http.get<Organization>(env.contextPath + env.apiPath + '/organizations/' + id);
  }

  save(organization: Organization): Observable<Organization> {
    const path: string = env.contextPath + env.apiPath + '/organizations/'

    return this.http.post<Organization>(path, organization).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.organization.save.result.success')
      }))
  }

  initNew(): Organization {
    const newOrganization = {
      id: null,
      parentOrganizationId: null,
      prefLabel: null,
      abbreviation: null,
      organizationUnit: [],
      personInRoles: [],
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
