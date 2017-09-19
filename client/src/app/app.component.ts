import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from "./services-common/growl-message.service";
import { Title } from '@angular/platform-browser'

import { PageIdentifier } from './utils/page-identifier'

import {Observable, Subscription} from 'rxjs';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/mergeMap';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  public PageIdentifier = PageIdentifier
  currentPageType: PageIdentifier
  mainContainerClasses: any = {}

  constructor(
    public growlMessageService: GrowlMessageService,
    public translateService: TranslateService,
    private titleService: Title,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    translateService.setDefaultLang('fi');
    translateService.use('fi');
  }

  ngOnInit() {
    this.router.events
      .filter((event) => event instanceof NavigationEnd)
      .map(() => this.activatedRoute)
      .map((route) => {
        while (route.firstChild) route = route.firstChild;
        return route;
      })
      .filter((route) => route.outlet === 'primary')
      .mergeMap((route) => route.data)
      .subscribe(event => {
        let titleTranslationKey: string = event['title']
        let pageType: PageIdentifier = event['pageType']

        this.setPageTitle(titleTranslationKey, pageType)

        this.currentPageType = pageType | this.currentPageType

        this.mainContainerClasses['main-container'] = true
        this.mainContainerClasses['has-sidebar'] = event['hasSidebar']

        this.mainContainerClasses['catalog'] = this.currentPageType == PageIdentifier.CATALOG
        this.mainContainerClasses['container'] = this.currentPageType == PageIdentifier.CATALOG

        this.mainContainerClasses['editor'] = this.currentPageType == PageIdentifier.EDITOR
        this.mainContainerClasses['container-fluid'] = this.currentPageType == PageIdentifier.EDITOR && !event['hasSidebar']
      });
  }

  private setPageTitle(translationKey: string, pageType: PageIdentifier):void {

    let validPageType: PageIdentifier = pageType != null ? pageType : this.currentPageType

    if(translationKey) {
      this.getPageTitle(translationKey).subscribe(title => {
        this.getPageTitleSuffix(validPageType).subscribe(suffix => {
          let fullTitle:string = suffix ? title + " - " + suffix: title
          this.titleService.setTitle(fullTitle)
        })
      })
    } // for routes without a set title, use only the generic suffix for titles
    else if(validPageType != null) {
      this.getPageTitleSuffix(validPageType).subscribe(suffix => {
        this.titleService.setTitle(suffix)
      })
    }
  }

  private getPageTitle(translationKey: string):Observable<string> {
    return this.translateService.get(translationKey)
  }

  private getPageTitleSuffix(pageType:PageIdentifier):Observable<string> {

    let suffix:Observable<string> = Observable.of("")

    if(pageType === PageIdentifier.CATALOG) {
      suffix = this.translateService.get('pageTitles.catalog.suffix');
    }

    else if(pageType === PageIdentifier.EDITOR) {
      suffix = this.translateService.get('pageTitles.editor.suffix');
    }
      return suffix
  }

}
