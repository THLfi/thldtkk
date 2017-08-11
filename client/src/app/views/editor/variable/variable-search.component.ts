import { ActivatedRoute, Router, UrlTree } from '@angular/router'
import { Component, OnInit } from '@angular/core'
import { Observable, Subscription } from 'rxjs'
import { TranslateService } from '@ngx-translate/core'
import { Location } from '@angular/common'

import { Dataset } from '../../../model2/dataset'
import { DatasetService } from '../../../services2/dataset.service'
import { Variable } from '../../../model2/variable'
import { VariableService } from '../../../services2/variable.service'
import { LangPipe } from '../../../utils/lang.pipe';

@Component({
  templateUrl: './variable-search.component.html',
    styleUrls: ['./variable-search.component.css']
})
export class VariableSearchComponent implements OnInit {

  language: string
  variables: Variable[]
  variableSearchSubscription: Subscription
  searchText: string

  constructor(private variableService: VariableService,
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
        this.searchVariables(this.searchText)
      }else{
        this.variableService.getAllVariables()
                    .subscribe(variables => this.variables = variables);
      }

    })
  }

  searchVariables(searchText: string) {
    if(this.variableSearchSubscription) {
      this.variableSearchSubscription.unsubscribe();
    }

    this.variableSearchSubscription = this.variableService.searchVariable(searchText)
      .subscribe(variables => this.variables = variables)

    this.updateQueryParam(searchText)
  }

  private updateQueryParam(searchText:string):void {
    let urlTree:UrlTree = this.router.parseUrl(this.router.url)
    urlTree.queryParams['query'] = this.searchText

    let updatedUrl = this.router.serializeUrl(urlTree);
    this.location.replaceState(updatedUrl);
  }
}
