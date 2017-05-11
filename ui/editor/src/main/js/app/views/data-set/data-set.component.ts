import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";
import { TranslateService } from "@ngx-translate/core";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { InstanceVariable } from '../../model/instance-variable';
import { Organization } from '../../model/organization';
import { OrganizationUnit } from '../../model/organization-unit';
import { Person } from "../../model/person";
import { PersonService } from "../../services/person.service";
import { Population } from "../../model/population";
import { RoleService } from '../../services/role.service';
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
    contactPerson : Person;
    owner : Person;

    constructor(
        private dataSetService: DataSetService,
        private personService : PersonService,
        private roleService: RoleService,
        private route: ActivatedRoute,
        private translateService: TranslateService
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
            this.dataSetService.getDataSetOrganizationUnits(dataSetId),
            this.dataSetService.getDataSetPersonsInRoles(dataSetId)
        ).subscribe(
            data => {
                this.dataSet = data[0],
                this.ownerOrganization = data[1][0],
                this.population = data[2][0],
                this.usageCondition = data[3][0],
                this.instanceVariables = data[4],
                this.lifecyclePhase = data[5][0],
                this.ownerOrganizationUnit = data[6][0];
                if (data[7]){
                  data[7].forEach(item => {
                      this.roleService.getRole(item.references.role[0].id)
                      .subscribe(role => {
                        if (role.properties.prefLabel[0].value == 'contact'){
                            this.personService.getPerson(item.references.person[0].id)
                            .subscribe(person => this.contactPerson = person);
                        } else if (role.properties.prefLabel[0].value == 'owner'){
                            this.personService.getPerson(item.references.person[0].id)
                            .subscribe(person => this.owner = person);
                        }
                      })
                  })
                }
            }
        );
    }

    confirmPublish(): void {
      this.translateService.get('confirmPublishDataset')
        .subscribe((message: string) => {
          if (confirm(message)) {
            this.dataSetService.publishDataSet(this.dataSet)
              .subscribe(dataSet => this.dataSet = dataSet)
          }
        })
    }

    confirmUnpublish(): void {
      this.translateService.get('confirmUnpublishDataset')
        .subscribe((message: string) => {
          if (confirm(message)) {
            this.dataSetService.unpublishDataSet(this.dataSet)
              .subscribe(dataSet => this.dataSet = dataSet)
          }
        })
    }
}
