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
    currentLang: string

    conceptSearchSubscription: Subscription
    conceptSearchResults: Concept[] = []

    savingInProgress: boolean = false

    constructor(
        private instanceVariableService: InstanceVariableService,
        private conceptService: ConceptService,
        private route: ActivatedRoute,
        private router: Router,
        private translateService: TranslateService
    ) {
        this.currentLang = translateService.currentLang
    }

    ngOnInit(): void {
        const datasetId = this.route.snapshot.params['datasetId']
        const instanceVariableId = this.route.snapshot.params['instanceVariableId']

        if (instanceVariableId) {
            this.instanceVariableService.getInstanceVariable(datasetId, instanceVariableId)
                .subscribe(instanceVariable => {
                    this.instanceVariable = this.initInstanceVariable(instanceVariable)
                })
        }
        else {
            this.instanceVariable = this.initInstanceVariable({
                id: null,
                prefLabel: null,
                description: null,
                referencePeriodStart: null,
                referencePeriodEnd: null,
                technicalName: null,
                conceptsFromScheme: []
            })
        }
    }

    private initInstanceVariable(instanceVariable: InstanceVariable): InstanceVariable {
        return this.initProperties(instanceVariable, ['prefLabel', 'description'])
    }

    private initProperties(instanceVariable: InstanceVariable, properties: string[]): InstanceVariable {
        properties.forEach(property => {
            if (!instanceVariable[property]) {
                instanceVariable[property] = {}
            }
            if (!instanceVariable[property][this.currentLang]) {
                instanceVariable[property][this.currentLang] = ''
            }
        })
        return instanceVariable
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
        if (concept.prefLabel.hasOwnProperty(lang) && lang != this.currentLang) {
          languages.push(lang)
        }
      }
      return languages
    }

    save(): void {
        this.savingInProgress = true
        const datasetId = this.route.snapshot.params['datasetId']
        this.instanceVariableService.saveInstanceVariable(datasetId, this.instanceVariable)
            .subscribe(instanceVariable => {
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
