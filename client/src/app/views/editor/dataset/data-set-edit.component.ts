import {ActivatedRoute, Router} from '@angular/router';
import {Component, OnInit} from '@angular/core';
import {Observable, Subscription} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

import {Concept} from '../../../model2/concept';
import {ConceptService} from '../../../services2/concept.service';
import {Dataset} from '../../../model2/dataset';
import {DatasetService} from '../../../services2/dataset.service';
import {LifecyclePhase} from "../../../model2/lifecycle-phase";
import {LifecyclePhaseService} from "../../../services2/lifecycle-phase.service";
import {NodeUtils} from '../../../utils/node-utils';
import {Organization} from "../../../model2/organization";
import {OrganizationService} from "../../../services2/organization.service";
import {OrganizationUnit} from "../../../model2/organization-unit";
import {OrganizationUnitService} from "../../../services2/organization-unit.service";
import {DatasetType} from "../../../model2/dataset-type"
import {DatasetTypeService} from "../../../services2/dataset-type.service"
import {Population} from "../../../model2/population";
import {UsageCondition} from "../../../model2/usage-condition";
import {UsageConditionService} from "../../../services2/usage-condition.service";

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
    allUsageConditions: UsageCondition[];
    allDatasetTypes: DatasetType[];
    language: string;
    lifecyclePhase: LifecyclePhase;

    conceptSearchSubscription: Subscription;
    conceptSearchResults: Concept[] = [];

    constructor(
        private datasetService: DatasetService,
        private lifecyclePhaseService: LifecyclePhaseService,
        private nodeUtils: NodeUtils,
        private organizationService: OrganizationService,
        private organizationUnitService: OrganizationUnitService,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService,
        private usageConditionService: UsageConditionService,
        private datasetTypeService: DatasetTypeService,
        private conceptService: ConceptService
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
                numberOfObservationUnits: null,
                datasetType: null,
                comment: null,
                conceptsFromScheme: [],
                links: []
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
        this.datasetTypeService.getDatasetTypes()
            .subscribe(datasetTypes => this.allDatasetTypes = datasetTypes)
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

    searchConcept(event: any): void {
        const searchText: string = event.query
        if (this.conceptSearchSubscription) {
            // Cancel possible on-going search
            this.conceptSearchSubscription.unsubscribe()
        }
        this.conceptSearchSubscription = this.conceptService.searchConcept(searchText)
            .subscribe(concepts => this.conceptSearchResults = concepts)
    }

    getConceptLanguages(concept: Concept): any {
        const languages = []
        for (let lang in concept.prefLabel) {
            if (concept.prefLabel.hasOwnProperty(lang) && lang != this.language) {
                languages.push(lang)
            }
        }
        return languages
    }

    addLink() {
        if (!this.dataset.links) {
            this.dataset.links = []
        }
        var link = {prefLabel: null, linkUrl: null};
        ['prefLabel', 'linkUrl']
            .forEach(item => this.createEmptyTranslateableProperty(link, item, this.language))
        this.dataset.links.push(link);
    }

    removeLink(link) {
        let index: number = this.dataset.links.indexOf(link);
        if (index !== -1) {
            this.dataset.links.splice(index, 1);
        }
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
