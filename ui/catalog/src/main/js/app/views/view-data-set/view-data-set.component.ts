import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';

@Component({
    templateUrl: './view-data-set.component.html',
    styleUrls: ['./view-data-set.component.css']
})
export class ViewDataSetComponent implements OnInit {
    dataSet: DataSet;

    constructor(
        private dataSetService: DataSetService,
        private route: ActivatedRoute
    ) {}

    ngOnInit() {
        this.dataSetService.getDataSet(this.route.snapshot.params['id'])
            .subscribe(dataSet => this.dataSet = dataSet);
    }
}
