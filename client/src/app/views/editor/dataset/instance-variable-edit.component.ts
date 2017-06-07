import {ActivatedRoute, Router} from '@angular/router';
import {Component, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';

import {Concept} from '../../../model2/concept';
import {ConceptService} from '../../../services2/concept.service';
import {InstanceVariable} from '../../../model2/instance-variable';
import {InstanceVariableService} from '../../../services2/instance-variable.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './instance-variable-edit.component.html'
})
export class InstanceVariableEditComponent implements OnInit {

    instanceVariable: InstanceVariable
    language: string

    conceptSearchSubscription: Subscription
    conceptSearchResults: Concept[] = []
    freeConcepts: string[] = []

    savingInProgress: boolean = false

    constructor(
        private instanceVariableService: InstanceVariableService,
        private conceptService: ConceptService,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService
    ) {
        this.language = translateService.currentLang
    }

    ngOnInit(): void {
        const datasetId = this.route.snapshot.params['datasetId']
        const instanceVariableId = this.route.snapshot.params['instanceVariableId']

        if (instanceVariableId) {
            this.instanceVariableService.getInstanceVariable(datasetId, instanceVariableId)
                .subscribe(instanceVariable => {
                  this.initInstanceVariable(instanceVariable)
                  this.instanceVariable = instanceVariable
                })
        }
        else {
            const instanceVariable = {
                id: null,
                prefLabel: null,
                description: null,
                referencePeriodStart: null,
                referencePeriodEnd: null,
                technicalName: null,
                conceptsFromScheme: [],
                freeConcepts: null
            }
            this.initInstanceVariable(instanceVariable)
            this.instanceVariable = instanceVariable
        }
    }

    private initInstanceVariable(instanceVariable: InstanceVariable): void {
      this.initProperties(instanceVariable, ['prefLabel', 'description', 'freeConcepts'])
      if (instanceVariable.freeConcepts && instanceVariable.freeConcepts[this.language]) {
        this.freeConcepts = instanceVariable.freeConcepts[this.language].split(';')
      }
    }

    private initProperties(instanceVariable: InstanceVariable, properties: string[]): void {
        properties.forEach(property => {
            if (!instanceVariable[property]) {
                instanceVariable[property] = {}
            }
            if (!instanceVariable[property][this.language]) {
                instanceVariable[property][this.language] = ''
            }
        })
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

    save(): void {
        this.savingInProgress = true

        this.instanceVariable.freeConcepts[this.language] = this.freeConcepts.join(';')

        const datasetId = this.route.snapshot.params['datasetId']
        this.instanceVariableService.saveInstanceVariable(datasetId, this.instanceVariable)
            .subscribe(instanceVariable => {
                this.initInstanceVariable(instanceVariable)
                this.instanceVariable = instanceVariable
                this.savingInProgress = false
                this.goBackToViewInstanceVariable()
            })
    }

    confirmRemove(): void {
        this.translateService.get('confirmInstanceVariableDelete')
            .subscribe((message: string) => {
                if (confirm(message)) {
                    this.savingInProgress = true

                    const datasetId = this.route.snapshot.params['datasetId'];
                    const instanceVariableId = this.route.snapshot.params['instanceVariableId'];

                    this.instanceVariableService.deleteInstanceVariable(datasetId, instanceVariableId).subscribe(() => {
                        this.savingInProgress = false
                        this.goBackToViewDataset()
                      }
                    );
                }
            })
    }

    goBackToViewInstanceVariable(): void {
      this.router.navigate(['/editor/datasets', this.route.snapshot.params['datasetId'], 'instanceVariables', this.route.snapshot.params['instanceVariableId']])
    }

    goBackToViewDataset(): void {
      this.router.navigate(['/editor/datasets', this.route.snapshot.params['datasetId']])
    }

}
