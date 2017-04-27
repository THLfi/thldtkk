import { Component, OnInit } from '@angular/core';
import 'rxjs/add/operator/toPromise';

import { DataSetService } from "../../services/data-set.service";
import { DataSet } from "../../model/data-set";

@Component({
    templateUrl: './data-set-list.component.html'
})
export class DataSetListComponent implements OnInit {
    dataSets: DataSet[]

    constructor (
        private dataSetService: DataSetService
    ) {}

    ngOnInit(): void {
        this.dataSetService.getAllDataSets()
            .subscribe(dataSets => this.dataSets = dataSets);
    }
}
