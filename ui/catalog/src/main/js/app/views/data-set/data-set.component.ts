import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { InstanceVariable } from '../../model/instance-variable';
import { LifecyclePhase } from '../../model/lifecycle-phase';
import { Organization } from '../../model/organization';
import { Person } from "../../model/person";
import { PersonService } from '../../services/person.service';
import { Population } from "../../model/population";
import { RoleService } from '../../services/role.service';
import { UsageCondition } from "../../model/usage-condition";

@Component({
  templateUrl: './data-set.component.html',
  styleUrls: ['./data-set.component.css']
})
export class DataSetComponent implements OnInit {

    dataSet: DataSet;
    ownerOrganization: Organization;
    population: Population;
    usageCondition: UsageCondition;
    instanceVariables: InstanceVariable[];
    lifecyclePhase: LifecyclePhase;
    contactPerson: Person;

    constructor(
        private dataSetService: DataSetService,
        private personService: PersonService,
        private roleService: RoleService,
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
            this.dataSetService.getDataSetUsageCondition(datasetId),
            this.dataSetService.getDataSetInstanceVariables(datasetId),
            this.dataSetService.getDataSetLifecyclePhases(datasetId),
            this.dataSetService.getDataSetPersonsInRoles(datasetId)
        ).subscribe(
            data => {
                this.dataSet = data[0],
                this.ownerOrganization = data[1][0],
                this.population = data[2][0],
                this.usageCondition = data[3][0],
                this.instanceVariables = data[4],
                this.lifecyclePhase = data[5][0]
                if (data[6]){
                    data[6].forEach(item => {
                        this.roleService.getRole(item.references.role[0].id)
                        .subscribe(role => {
                            if (role.properties.prefLabel[0].value == 'contact'){
                                this.personService.getPerson(item.references.person[0].id)
                                .subscribe(person => this.contactPerson = person);
                            }
                        })
                    })
                }
            })
    }

}
