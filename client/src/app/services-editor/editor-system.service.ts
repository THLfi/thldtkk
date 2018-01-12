import { Http, Headers, RequestOptions } from '@angular/http'
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { environment as env } from '../../environments/environment'
import { Observable } from 'rxjs'
import 'rxjs/add/operator/map'

import { GrowlMessageService } from '../services-common/growl-message.service'
import { NodeUtils } from '../utils/node-utils'
import { System } from '../model2/system'

@Injectable()
export class EditorSystemService {

  constructor(
    private translateService: TranslateService,
    private growlMessageService: GrowlMessageService,
    private nodeUtils: NodeUtils,
    private http: Http) {}

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
    return this.http.get(env.contextPath + '/api/v3/editor/systems')
      .map(response => response.json() as System[])
  }

  getSystem(id: string): Observable<System> {
    return this.http.get(env.contextPath + '/api/v3/editor/systems/' + id)
      .map(response => response.json() as System)
  }

  save(system: System): Observable<System> {
    return this.saveSystemInternal(system)
      .do(dataset => {
        this.growlMessageService.buildAndShowMessage('success', 'operations.system.save.result.success')
      })
  }

  private saveSystemInternal(system: System): Observable<System> {
    const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8' })
    const options = new RequestOptions({ headers: headers })

    return this.http.post(env.contextPath + '/api/v3/editor/systems', system, options)
      .map(response => response.json() as System)
  }
  
}
