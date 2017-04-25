import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { Organization } from '../../model/organization';
import { OrganizationService } from '../../services/organization.service';

@Component({
    templateUrl: './data-set.component.html'
})
export class DataSetComponent implements OnInit {
    dataSet: DataSet;
    ownerOrganization: Organization;

    constructor(
        private dataSetService: DataSetService,
        private organizationService: OrganizationService,
        private route: ActivatedRoute
    ) {}

    ngOnInit() {
        this.getDataSet();
    }

    private getDataSet() {
        this.dataSetService.getDataSet(this.route.snapshot.params['id'])
            .subscribe(dataSet => {
                this.dataSet = dataSet;
                this.getOwnerOrganization(dataSet);
            });
    }

    private getOwnerOrganization(dataSet) {
        if (dataSet.references.owner && dataSet.references.owner.length) {
            const ownerOrganizationId: String = <String> dataSet.references.owner[0].id;
            this.organizationService.getOrganization(ownerOrganizationId)
                .subscribe(organization => this.ownerOrganization = organization);
        }
    }
}
