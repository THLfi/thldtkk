import { ActivatedRoute, Router, UrlTree } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable, Subscription } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'
import { Location } from '@angular/common'

import { Dataset } from '../../../model2/dataset'
import { DatasetService } from '../../../services2/dataset.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableService } from '../../../services2/instance-variable.service'
import { LangPipe } from '../../../utils/lang.pipe';

@Component({
  templateUrl: './instance-variable-search.component.html',
    styleUrls: ['./instance-variable-search.component.css']
})
export class InstanceVariableSearchComponent implements OnInit {

  language: string
  instanceVariables: InstanceVariable[]
  instanceVariableSearchSubscription: Subscription
  searchText: string

  constructor(private instanceVariableService: InstanceVariableService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location,
              private translateService: TranslateService) {
    this.language = this.translateService.currentLang
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.searchText = params['query'];

      if(this.searchText != null && this.searchText != "") {
        this.searchInstanceVariables(this.searchText)
      }

    })
  }

  searchInstanceVariables(searchText: string) {
    if(this.instanceVariableSearchSubscription) {
      this.instanceVariableSearchSubscription.unsubscribe();
    }

    this.instanceVariableSearchSubscription = this.instanceVariableService.searchInstanceVariable(searchText)
      .subscribe(instanceVariables => this.instanceVariables = instanceVariables)

    this.updateQueryParam(searchText)
  }

  private updateQueryParam(searchText:string):void {
    let urlTree:UrlTree = this.router.parseUrl(this.router.url)
    urlTree.queryParams['query'] = this.searchText

    let updatedUrl = this.router.serializeUrl(urlTree);
    this.location.replaceState(updatedUrl);
  }
}