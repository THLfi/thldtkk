import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { InstanceVariable } from '../../model/instance-variable';
import { Organization } from '../../model/organization';
import { Population } from "../../model/population";

@Component({
    templateUrl: './data-set.component.html'
})
export class DataSetComponent implements OnInit {

    dataSet: DataSet;
    ownerOrganization: Organization;
    population: Population;
    instanceVariables: InstanceVariable[];

    constructor(
        private dataSetService: DataSetService,
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
            this.dataSetService.getDataSetPopulations(datasetId),
            this.dataSetService.getDataSetInstanceVariables(datasetId)
        ).subscribe(
            data => {
                this.dataSet = data[0],
                this.ownerOrganization = data[1][0],
                this.population = data[2][0],
                this.instanceVariables = data[3]
            }
        );
    }

}
