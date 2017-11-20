import { ActivatedRoute, Router, UrlTree } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable, Subject } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'
import { Location } from '@angular/common'

import { InstanceVariable } from '../../../model2/instance-variable'
import { Variable } from '../../../model2/variable'
import { PublicInstanceVariableService } from '../../../services-public/public-instance-variable.service'

// Observable operators
import 'rxjs/add/operator/switchMap'
import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/debounceTime'
import 'rxjs/add/operator/distinctUntilChanged'

import 'rxjs/add/observable/of';

@Component({
  templateUrl: './instance-variable-search.component.html',
    styleUrls: ['./instance-variable-search.component.css']
})
export class InstanceVariableSearchComponent implements OnInit {

  language: string
  instanceVariables: InstanceVariable[]
  variables: Variable[]
  searchText: string
  maxResults: number
  searchTerms: Subject<string>
  searchInProgress: boolean
  searchingMoreResults: boolean
  latestLookupTerm: string

  static readonly defaultMaxResults = 100
  static readonly searchDelay = 500;

  constructor(private instanceVariableService: PublicInstanceVariableService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location,
              private translateService: TranslateService) {
    this.language = this.translateService.currentLang
    this.searchTerms = new Subject<string>()
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.searchText = params['query'];
      this.maxResults = Number(params['max'] || InstanceVariableSearchComponent.defaultMaxResults);
      if(this.searchText != null && this.searchText != "") {
        this.searchInProgress = true
        this.searchInstanceVariables(this.searchText).subscribe(instanceVariables => {
          this.instanceVariables = instanceVariables
          this.variables = this.extractVariables(this.instanceVariables)
          this.latestLookupTerm = this.searchText
          this.searchInProgress = false
        })
        this.updateQueryParam(this.searchText)
      }
    })
    this.initSearchSubscription(this.searchTerms)
  }

  delayedSearchInstanceVariables(searchText:string): void {
    this.searchTerms.next(searchText)
  }

  loadMoreResults(): void {
    this.maxResults += 100
    this.searchingMoreResults = true
    this.searchInProgress = true;

    this.instanceVariableService.search(this.searchText, this.maxResults)
      .subscribe(instanceVariables => {
        this.updateQueryParam(this.searchText)
        this.instanceVariables = instanceVariables;
        this.variables = this.extractVariables(this.instanceVariables)
        this.searchInProgress = false;
        this.searchingMoreResults = false;
      });
  }

  searchInstanceVariables(searchText: string): Observable<InstanceVariable[]> {
    return this.instanceVariableService.search(searchText, this.maxResults)
  }

  private updateQueryParam(searchText:string): void {
    let urlTree:UrlTree = this.router.parseUrl(this.router.url)
    urlTree.queryParams['query'] = this.searchText
    urlTree.queryParams['max'] = String(this.maxResults)

    let updatedUrl = this.router.serializeUrl(urlTree);
    this.location.replaceState(updatedUrl);
  }

  private extractVariables(instanceVariables:InstanceVariable[]):Variable[] {
    let variablesById: {[id:string]:Variable} = {};
    let variables: Variable[] = []

    instanceVariables
      .filter(iv => iv.variable)
      .map(iv => variablesById[iv.variable.id] = iv.variable)

    for (let variableId in variablesById) {
      variables.push(variablesById[variableId])
    }

    return variables;
  }

  private initSearchSubscription(searchTerms:Subject<string> ): void {
    searchTerms.debounceTime(InstanceVariableSearchComponent.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.searchInProgress = true;
        this.latestLookupTerm = term;
        this.maxResults = InstanceVariableSearchComponent.defaultMaxResults
        return term ? this.searchInstanceVariables(term) : Observable.of<InstanceVariable[]>([])
      })
      .catch(error => {
        this.initSearchSubscription(searchTerms)
        return Observable.of<InstanceVariable[]>([])
      })
      .subscribe(instanceVariables => {
        this.updateQueryParam(this.searchText)
        this.instanceVariables = instanceVariables
        this.variables = this.extractVariables(instanceVariables)
        this.searchInProgress = false})
  }
}
