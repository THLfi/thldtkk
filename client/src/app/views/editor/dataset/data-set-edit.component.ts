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
import {DatasetTypeItem} from "../../../model2/dataset-type-item"
import {DatasetTypeService} from "../../../services2/dataset-type.service"
import {Population} from "../../../model2/population";
import {UsageCondition} from "../../../model2/usage-condition";
import {UsageConditionService} from "../../../services2/usage-condition.service";

import {SelectItem} from "primeng/primeng"
import {LangPipe} from '../../../utils/lang.pipe'

@Component({
    templateUrl: './data-set-edit.component.html',
    providers: [LangPipe]
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
    language: string;
    lifecyclePhase: LifecyclePhase;
    conceptSearchSubscription: Subscription;
    conceptSearchResults: Concept[] = [];
    freeConcepts: string[] = [];

    // separate type labels and values for multiselect, id of datasetType as value for select
    datasetTypeItems: DatasetTypeItem[] = [];
    selectedDatasetTypeItems: string[] = [];
    datasetTypesById: {[datasetTypeId: string]: DatasetType} = {};

    savingInProgress: boolean = false

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
        private conceptService: ConceptService,
        private langPipe: LangPipe
    ) {
        this.language = this.translateService.currentLang
    }


    ngOnInit() {
        this.getDataset();
    }

    private getDataset() {
        const datasetId = this.route.snapshot.params['id'];
        if (datasetId) {
            Observable.forkJoin(
                this.datasetService.getDataset(datasetId)
            ).subscribe(
                data => {
                    this.dataset = this.initializeDataSetProperties(data[0])
                    this.selectedDatasetTypeItems = this.initializeSelectedDatasetTypes(this.dataset);
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
                comment: null,
                conceptsFromScheme: [],
                links: [],
                freeConcepts: null,
                datasetTypes: []
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
            .subscribe(datasetTypes => {

                let unsortedDatasetTypeItems: Array<DatasetTypeItem> = []

                datasetTypes.forEach(datasetType => {
                    let translatedTypeLabel = this.langPipe.transform(datasetType.prefLabel);
                    unsortedDatasetTypeItems.push(new DatasetTypeItem(translatedTypeLabel, datasetType.id));

                    this.datasetTypesById[datasetType.id] = datasetType;
                }
                );

                this.datasetTypeItems = unsortedDatasetTypeItems.sort(DatasetTypeItem.compare);
            })

    }

    private initializeDataSetProperties(dataset: Dataset): Dataset {
        [
            'prefLabel',
            'abbreviation',
            'altLabel',
            'description',
            'researchProjectURL',
            'registryPolicy',
            'usageConditionAdditionalInformation',
            'freeConcepts'
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

        if (dataset.freeConcepts && dataset.freeConcepts[this.language]) {
            this.freeConcepts = dataset.freeConcepts[this.language].split(';')
        }

        return dataset;
    }

    private initializeSelectedDatasetTypes(dataset: Dataset): string[] {
        let storedDatasetTypeItems = []
        dataset.datasetTypes.forEach(datasetType => {storedDatasetTypeItems.push(datasetType.id)});
        return storedDatasetTypeItems;
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

    private resolveSelectedDatasetTypes(): DatasetType[] {

        let selectedDatasetTypes: Array<DatasetType> = [];

        if (this.selectedDatasetTypeItems) {
            this.selectedDatasetTypeItems.forEach(datasetTypeId => {
                let datasetType: DatasetType = this.datasetTypesById[datasetTypeId];
                selectedDatasetTypes.push(datasetType);
            });
        }

        return selectedDatasetTypes;
    }

    importXml(event) {
        this.datasetService.importDataset(event).subscribe(
            data => {
                this.dataset = this.initializeDataSetProperties(data);
            }, error => {
                console.log(error);
            });
    }

    save() {
        this.savingInProgress = true

        if (this.ownerOrganizationUnit) {
            this.dataset.ownerOrganizationUnit = [];
            this.dataset.ownerOrganizationUnit.push(this.ownerOrganizationUnit);
        }

        this.dataset.freeConcepts[this.language] = this.freeConcepts.join(';')

        this.dataset.datasetTypes = this.resolveSelectedDatasetTypes();


        this.datasetService.saveDataset(this.dataset)
            .subscribe(savedDataset => {
                this.dataset = savedDataset;
                this.savingInProgress = false
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
