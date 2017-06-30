import { Component, OnInit } from '@angular/core';
import 'rxjs/add/operator/toPromise';

import { DatasetService } from "../../../services2/dataset.service";
import { Dataset } from "../../../model2/dataset";

@Component({
    templateUrl: './data-set-list.component.html'
})
export class DataSetListComponent implements OnInit {
    datasets: Dataset[] = []

    constructor (
        private dataSetService: DatasetService
    ) {}

    ngOnInit(): void {
        this.dataSetService.getAllDatasets()
            .subscribe(dataSets => this.datasets = dataSets);
    }
}
