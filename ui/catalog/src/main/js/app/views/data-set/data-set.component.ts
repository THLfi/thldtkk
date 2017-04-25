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
        this.dataSetService.getDataSet(this.route.snapshot.params['id'])
            .subscribe(dataSet => {
                this.dataSet = dataSet;
                this.getOwnerOrganization(dataSet);
                this.getInstanceVariables(dataSet);
            });
    }

    private getOwnerOrganization(dataSet) {
        if (dataSet.references.owner && dataSet.references.owner.length) {
            const ownerOrganizationId: String = <String>dataSet.references.owner[0].id;
            this.organizationService.getOrganization(ownerOrganizationId)
                .subscribe(organization => this.ownerOrganization = organization);
        }
    }

    private getInstanceVariables(dataSet) {
        if (dataSet.references.instanceVariable && dataSet.references.instanceVariable.length) {
            this.instanceVariables = [];

            dataSet.references.instanceVariable.forEach(variable => {
                this.instanceVariableService.getInstanceVariable(variable.id)
                    .subscribe(v => this.instanceVariables.push(v));
            });
        }
    }

}
