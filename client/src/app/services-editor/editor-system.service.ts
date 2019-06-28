
import {tap} from 'rxjs/operators';
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env } from '../../environments/environment'
import { Observable } from 'rxjs'


import { GrowlMessageService } from '../services-common/growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { System } from '../model2/system'
import { HttpClient } from '@angular/common/http';

@Injectable()
export class EditorSystemService {

  constructor(
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private http: HttpClient
    ) {}

  public initNew(): System {
    return this.initializeProperties({
      id: null,
      prefLabel: null,
      ownerOrganization: null
    })
  }

  initializeProperties(system: System): System {
    this.initProperties(system, [
      'prefLabel'
    ])

    const link = {
        id: null,
        prefLabel: null,
        linkUrl: null
    }

    this.initProperties(link, [ 'prefLabel', 'linkUrl' ])
    system.link = link

    return system
  }

  private initProperties(node: any, properties: string[]): void {
    this.nodeUtils.initLangValuesProperties(node, properties, [ this.translateService.currentLang ])
  }

  getAll(): Observable<System[]> {
    return this.http.get<System[]>(env.contextPath + env.apiPath + '/editor/systems');
  }

  getSystem(id: string): Observable<System> {
    return this.http.get<System>(env.contextPath + env.apiPath + '/editor/systems/' + id);
  }

  save(system: System): Observable<System> {
    return this.saveSystemInternal(system).pipe(
      tap(() => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.system.save.result.success')
      }))
  }

  private saveSystemInternal(system: System): Observable<System> {

    return this.http.post<System>(env.contextPath + env.apiPath + '/editor/systems', system);
  }

}
