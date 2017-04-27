import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { InstanceVariable } from '../../model/instance-variable';
import { Organization } from '../../model/organization';

@Component({
    templateUrl: './data-set.component.html'
})
export class DataSetComponent implements OnInit {

    dataSet: DataSet;
    ownerOrganization: Organization;
    instanceVariables: InstanceVariable[];

    constructor(
        private dataSetService: DataSetService,
        private route: ActivatedRoute
    ) {}

    ngOnInit() {
        this.getDataSet();
    }

    private getDataSet() {
        const dataSetId = this.route.snapshot.params['id'];

        Observable.forkJoin(
            this.dataSetService.getDataSet(dataSetId),
            this.dataSetService.getDataSetOwners(dataSetId),
            this.dataSetService.getDataSetInstanceVariables(dataSetId)
        ).subscribe(
            data => {
                this.dataSet = data[0],
                this.ownerOrganization = data[1][0],
                this.instanceVariables = data[2]
            }
        );
    }

}
