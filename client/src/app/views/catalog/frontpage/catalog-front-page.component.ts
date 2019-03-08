import { Component, OnInit } from '@angular/core'
import { Router, NavigationExtras} from '@angular/router'
import 'rxjs/add/operator/toPromise'

import { Dataset } from '../../../model2/dataset'
import { Study } from '../../../model2/study'
import { PublicStudyService } from '../../../services-public/public-study.service'

@Component({
    templateUrl: './catalog-front-page.component.html',
    styleUrls: ['./catalog-front-page.component.css']
})
export class CatalogFrontPageComponent implements OnInit {

    studies: Study[] = []
    datasets: Dataset[] = []
    searchText: string
    isLoadingStudies: boolean

    constructor (
        private studyService: PublicStudyService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.isLoadingStudies = true
        this.studyService.getRecentStudies()
            .subscribe(studies => {
                this.studies = studies
                this.isLoadingStudies = false
            });
    }

    searchVariables(searchText:string): void {
        let navigationExtras: NavigationExtras = {
            queryParams:{ 'query': searchText }
        }
        this.router.navigate(['/catalog/instancevariables/'],navigationExtras)
    }

}
