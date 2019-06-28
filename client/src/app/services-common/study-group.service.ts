
import {empty as observableEmpty,  Observable } from 'rxjs';
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { NodeUtils } from '../utils/node-utils'
import { StudyGroup } from '../model2/study-group'
import { StringUtils } from '../utils/string-utils'
import { Study } from '../model2/study';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class StudyGroupService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: HttpClient,
    private translateService: TranslateService
  ) { }

  findByOwnerOrganizationId(organizationId: string): Observable<StudyGroup[]> {
    if (StringUtils.isBlank(organizationId)) {
      return observableEmpty()
    }
    else {
      const url = env.contextPath
        + env.apiPath
        + '/studyGroups?ownerOrganizationId='
        + organizationId

      return this.http.get<StudyGroup[]>(url);
    }
  }

  save(studyGroup: StudyGroup): Observable<StudyGroup> {
    const path: string = env.contextPath + env.apiPath + '/studyGroups/'

    return this.http.post<StudyGroup>(path, studyGroup);
  }

  initNew(): StudyGroup {
    const studyGroup = {
      id: null,
      prefLabel: null,
      ownerOrganization: null
    }

    this.nodeUtils.initLangValuesProperties(studyGroup,
      [ 'prefLabel', 'description' ],
      [ this.translateService.currentLang ])

    return studyGroup
  }

  get(studyGroupId: string): Observable<StudyGroup> {
    return this.http.get<StudyGroup>(env.contextPath + env.apiPath + '/studyGroups/' + studyGroupId);
  }

  getStudies(studyGroupId: string): Observable<Study[]> {
    const url = env.contextPath
      + env.apiPath
      + '/public/studyGroups/'
      + studyGroupId
      + '/studies'
    return this.http.get<Study[]>(url);
  }
}
