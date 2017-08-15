import { ActivatedRoute, Router, UrlTree } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable, Subject } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'
import { Location } from '@angular/common'

import { Dataset } from '../../../model2/dataset'
import { DatasetService } from '../../../services2/dataset.service'
import { InstanceVariable } from '../../../model2/instance-variable'
import { InstanceVariableService } from '../../../services2/instance-variable.service'
import { LangPipe } from '../../../utils/lang.pipe';

// Observable operators
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import 'rxjs/add/observable/of';

@Component({
  templateUrl: './instance-variable-search.component.html',
    styleUrls: ['./instance-variable-search.component.css']
})
export class InstanceVariableSearchComponent implements OnInit {

  language: string
  instanceVariables: InstanceVariable[]
  searchText: string
  searchTerms: Subject<string>
  searchInProgress: boolean
  latestLookupTerm: string

  static readonly searchDelay = 500;

  constructor(private instanceVariableService: InstanceVariableService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location,
              private translateService: TranslateService) {
    this.language = this.translateService.currentLang
    this.searchTerms = new Subject<string>();
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.searchText = params['query'];
      if(this.searchText != null && this.searchText != "") {
        this.searchInstanceVariables(this.searchText).subscribe(instanceVariables => this.instanceVariables = instanceVariables)
        this.updateQueryParam(this.searchText)
      }
    })
    this.initSearchSubscription(this.searchTerms)
  }

  delayedSearchInstanceVariables(searchText:string): void {
      this.searchTerms.next(searchText)
  }

  searchInstanceVariables(searchText: string):Observable<InstanceVariable[]> {
    return this.instanceVariableService.searchInstanceVariable(searchText);
  }

  private updateQueryParam(searchText:string):void {
    let urlTree:UrlTree = this.router.parseUrl(this.router.url)
    urlTree.queryParams['query'] = this.searchText

    let updatedUrl = this.router.serializeUrl(urlTree);
    this.location.replaceState(updatedUrl);
  }

  private initSearchSubscription(searchTerms:Subject<string> ): void {
    searchTerms.debounceTime(InstanceVariableSearchComponent.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.latestLookupTerm = term;
        this.searchInProgress = true;
        return term ? this.searchInstanceVariables(term) : Observable.of<InstanceVariable[]>([])
      })
      .catch(error => {
        this.initSearchSubscription(searchTerms)
        return Observable.of<InstanceVariable[]>([])
      })
      .subscribe(instanceVariables => {
        this.updateQueryParam(this.searchText)
        this.instanceVariables = instanceVariables
        this.searchInProgress = false})
  }

}