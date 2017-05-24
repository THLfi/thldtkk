import {ActivatedRoute, Router} from '@angular/router';
import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {TranslateService} from '@ngx-translate/core';

import {Dataset} from '../../../model2/dataset';
import {DatasetService} from '../../../services2/dataset.service';
import {Node} from "../../../model2/node";
import {Organization} from "../../../model2/organization";
import {OrganizationService} from "../../../services2/organization.service";
import {OrganizationUnit} from "../../../model2/organization-unit";
import {OrganizationUnitService} from "../../../services2/organization-unit.service";
import {PersonInRole} from "../../../model/person-in-role";
import {PersonInRoleService} from "../../../services/person-in-role.service";
import {Population} from "../../../model2/population";
import {PopulationService} from "../../../services/population.service";
import {RoleService} from "../../../services/role.service";
import {UsageCondition} from "../../../model2/usage-condition";
import {UsageConditionService} from "../../../services2/usage-condition.service";
import {NodeUtils} from "../../../utils/node-utils";
import {LifecyclePhase} from "../../../model2/lifecycle-phase";
import {LifecyclePhaseService} from "../../../services2/lifecycle-phase.service";

@Component({
    templateUrl: './data-set-edit.component.html'
})
export class DataSetEditComponent implements OnInit {

    dataset: Dataset;
    population: Population;
    usageCondition: UsageCondition;
    ownerOrganizationUnit: OrganizationUnit;
    allLifecyclePhases: LifecyclePhase[];
    allOrganizations: Organization[];
    allOrganizationUnits: OrganizationUnit[];
    allPersonsInRoleOwner: PersonInRole[];
    allPersonsInRoleContactPerson: PersonInRole[];
    allUsageConditions: UsageCondition[];
    language: string;
    lifecyclePhase: LifecyclePhase;
    personsInRoles: PersonInRole[];
    contactPerson: PersonInRole;
    owner: PersonInRole;

    constructor(
        private datasetService: DatasetService,
        private nodeUtils: NodeUtils,
        private personInRoleService: PersonInRoleService,
        private populationService: PopulationService,
        private organizationService: OrganizationService,
        private organizationUnitService: OrganizationUnitService,
        private roleService: RoleService,
        private usageConditionService: UsageConditionService,
        private lifecyclePhaseService: LifecyclePhaseService,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.getDataset();
        this.language = this.translateService.currentLang
    }

    private getDataset() {
        const datasetId = this.route.snapshot.params['id'];
        if (datasetId) {
            Observable.forkJoin(
                this.datasetService.getDataset(datasetId)
            ).subscribe(
                data => {
                    this.dataset = this.initializeDataSetProperties(data[0]);
                })
        } else {
            this.dataset = this.initializeDataSetProperties({
                id: null,
                prefLabel: null,
                altLabel: null,
                abbreviation: null,
                description: null,
                registryPolicy: null,
                researchProjectURL: null,
                usageConditionAdditionalInformation: null,
                published: null,
                referencePeriodStart: null,
                referencePeriodEnd: null,
                owner: null,
                ownerOrganizationUnit: [],
                usageCondition: null,
                lifecyclePhase: null,
                population: null,
                instanceVariables: [],
                comment: null
            });
        }
        this.organizationService.getAllOrganizations()
            .subscribe(organizations => this.allOrganizations = organizations)
        this.lifecyclePhaseService.getAllLifecyclePhases()
            .subscribe(lifecyclePhases => this.allLifecyclePhases = lifecyclePhases)
        this.usageConditionService.getAllUsageConditions()
            .subscribe(usageConditions => this.allUsageConditions = usageConditions)
        this.organizationUnitService.getAllOrganizationUnits()
            .subscribe(organizationUnits => this.allOrganizationUnits = organizationUnits)
    }

    private initializeDataSetProperties(dataset: Dataset): Dataset {
        [
            'prefLabel',
            'abbreviation',
            'altLabel',
            'description',
            'researchProjectURL',
            'registryPolicy',
            'usageConditionAdditionalInformation'
        ].forEach(item => this.createEmptyTranslateableProperty(dataset, item, this.language));
        ['lifecyclePhase', 'referencePeriodStart', 'referencePeriodEnd']
            .forEach(item => this.createNullProperty(dataset, item));
        if (dataset.population == null) {
            dataset.population = this.initializePopulationFields({
                prefLabel: null,
                description: null,
                geographicalCoverage: null,
                sampleSize: null,
                loss: null
            });
        }
        if (dataset.ownerOrganizationUnit.length > 0) {
            this.ownerOrganizationUnit = dataset.ownerOrganizationUnit[0];
        }
        /* TODO: personInRole[] */
        return dataset;
    }

    private createEmptyTranslateableProperty(node: any, propertyName: string, language: string) {
        if (!node[propertyName] || !node[propertyName][language]) {
            node[propertyName] = {
                [language]: null
            }
        }
    }

    private createNullProperty(node: any, propertyName: string) {
        if (!node[propertyName]) {
            node[propertyName] = null;
        }
    }

    private initializePopulationFields(population: Population): Population {
        ['prefLabel', 'geographicalCoverage', 'sampleSize', 'loss', 'description']
            .forEach(item => this.createEmptyTranslateableProperty(population, item, this.language))
        return population;
    }

    save() {
        if (this.ownerOrganizationUnit) {
            this.dataset.ownerOrganizationUnit = [];
            this.dataset.ownerOrganizationUnit.push(this.ownerOrganizationUnit);
        }
        this.datasetService.saveDataset(this.dataset)
            .subscribe(savedDataSet => {
                this.dataset = savedDataSet;
                this.goBack();
            });
    }

    goBack() {
        if (this.dataset.id) {
            this.router.navigate(['/editor/datasets', this.dataset.id]);
        } else {
            this.router.navigate(['/editor/datasets']);
        }
    }
}
