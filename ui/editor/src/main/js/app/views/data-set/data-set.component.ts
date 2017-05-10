import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { InstanceVariable } from '../../model/instance-variable';
import { Organization } from '../../model/organization';
import { OrganizationUnit } from '../../model/organization-unit';
import { Population } from "../../model/population";
import { UsageCondition } from "../../model/usage-condition";
import { LifecyclePhase } from "../../model/lifecycle-phase";

@Component({
    templateUrl: './data-set.component.html'
})
export class DataSetComponent implements OnInit {

    dataSet: DataSet;
    ownerOrganization: Organization;
    ownerOrganizationUnit : OrganizationUnit;
    population: Population;
    usageCondition: UsageCondition;
    instanceVariables: InstanceVariable[];
    lifecyclePhase : LifecyclePhase;

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
            this.dataSetService.getDataSetPopulations(dataSetId),
            this.dataSetService.getDataSetUsageCondition(dataSetId),
            this.dataSetService.getDataSetInstanceVariables(dataSetId),
            this.dataSetService.getLifecyclePhases(dataSetId),
            this.dataSetService.getDataSetOrganizationUnits(dataSetId)
        ).subscribe(
            data => {
                this.dataSet = data[0],
                this.ownerOrganization = data[1][0],
                this.population = data[2][0],
                this.usageCondition = data[3][0],
                this.instanceVariables = data[4],
                this.lifecyclePhase = data[5][0],
                this.ownerOrganizationUnit = data[6][0]
            }
        );
    }

}
