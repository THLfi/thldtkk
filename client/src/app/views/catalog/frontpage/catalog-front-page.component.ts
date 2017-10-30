import { Component, OnInit } from '@angular/core'
import { Router, NavigationExtras} from '@angular/router'
import 'rxjs/add/operator/toPromise'

import { Dataset } from '../../../model2/dataset'
import { PublicDatasetService } from '../../../services-public/public-dataset.service'

@Component({
    templateUrl: './catalog-front-page.component.html',
    styleUrls: ['./catalog-front-page.component.css']
})
export class CatalogFrontPageComponent implements OnInit {

    datasets: Dataset[] = []
    searchText: string
    isLoadingDatasets: boolean = false

    constructor (
        private datasetService: PublicDatasetService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.isLoadingDatasets = true;
        this.datasetService.getRecentDatasets()
            .subscribe(datasets => {
                this.datasets = datasets
                this.isLoadingDatasets = false
            });
    }

    searchVariables(searchText:string): void {
        let navigationExtras: NavigationExtras = {
            queryParams:{ 'query': searchText }
        }
        this.router.navigate(['/catalog/instancevariables/'],navigationExtras)
    }

}
