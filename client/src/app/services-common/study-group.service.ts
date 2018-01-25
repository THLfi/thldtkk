import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'

import { environment as env} from '../../environments/environment'

import { NodeUtils } from '../utils/node-utils'
import { StudyGroup } from '../model2/study-group'
import { StringUtils } from '../utils/string-utils'

@Injectable()
export class StudyGroupService {

  constructor(
    private nodeUtils: NodeUtils,
    private http: Http,
    private translateService: TranslateService
  ) { }

  findByOwnerOrganizationId(organizationId: string): Observable<StudyGroup[]> {
    if (StringUtils.isBlank(organizationId)) {
      return Observable.empty()
    }
    else {
      const url = env.contextPath
        + env.apiPath
        + '/studyGroups?ownerOrganizationId='
        + organizationId

      return this.http.get(url)
        .map(response => response.json() as StudyGroup[])
    }
  }

  save(studyGroup: StudyGroup): Observable<StudyGroup> {
    const path: string = env.contextPath + env.apiPath + '/studyGroups/'
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(path, studyGroup, options)
      .map(response => response.json() as StudyGroup)
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

}
