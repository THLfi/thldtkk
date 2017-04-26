import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { InstanceVariable } from '../../model/instance-variable';
import { InstanceVariableService } from '../../services/instance-variable.service';
import { Organization } from '../../model/organization';
import { OrganizationService } from '../../services/organization.service';

@Component({
    templateUrl: './data-set.component.html'
})
export class DataSetComponent implements OnInit {

    dataSet: DataSet;
    ownerOrganization: Organization;
    instanceVariables: InstanceVariable[];

    constructor(
        private dataSetService: DataSetService,
        private organizationService: OrganizationService,
        private instanceVariableService: InstanceVariableService,
        private route: ActivatedRoute
    ) { }

    ngOnInit() {
        this.getDataSet();
    }

    private getDataSet() {
        const datasetId = this.route.snapshot.params['id'];

        Observable.forkJoin(
            this.dataSetService.getDataSet(datasetId),
            this.dataSetService.getDataSetOwners(datasetId),
            this.dataSetService.getDataSetInstanceVariables(datasetId)
        ).subscribe(
            data => {
                this.dataSet = data[0],
                this.ownerOrganization = data[1][0],
                this.instanceVariables = data[2]
            }
        );
    }

}
