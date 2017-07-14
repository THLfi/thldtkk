import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, NavigationExtras} from '@angular/router';
import 'rxjs/add/operator/toPromise';

import { DatasetService } from "../../../services2/dataset.service";
import { Dataset } from "../../../model2/dataset";

@Component({
    templateUrl: './catalog-front-page.component.html',
    styleUrls: ['./catalog-front-page.component.css']
})
export class CatalogFrontPageComponent implements OnInit {
    
    datasets: Dataset[] = []
    searchText: string

    constructor (
        private dataSetService: DatasetService,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.dataSetService.getRecentDatasets()
            .subscribe(datasets => this.datasets = datasets);
    }

    searchVariables(searchText:string): void {
        let navigationExtras: NavigationExtras = {
            queryParams:{ 'query': searchText }
        }
        this.router.navigate(['/catalog/instancevariables/'],navigationExtras)
    }
}
