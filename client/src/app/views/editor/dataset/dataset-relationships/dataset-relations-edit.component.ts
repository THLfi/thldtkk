import { Component, Input, OnInit } from '@angular/core'

import { SelectItem } from 'primeng/components/common/api'
import { TranslateService } from '@ngx-translate/core'

import { Dataset } from '../../../../model2/dataset'
import { DatasetRelationType } from './dataset-relation-type'
import { EditorDatasetService } from '../../../../services-editor/editor-dataset.service'
import { LangPipe } from '../../../../utils/lang.pipe'

class DatasetSelectItem implements SelectItem {
  constructor(
    public label: string,
    public value: any,
    public dataset: Dataset
  ) { }
}

@Component({
  selector: 'dataset-relations-edit',
  template: `
<label for="datasetRelations" class="field-label">{{ 'datasetRelations.label' | translate }}</label>
<ng-container *ngIf="dataset.predecessors.length; else noDatasetRelations;">
  <p>{{ 'datasetRelations.predecessors' | translate }}</p>
  <ul>
    <li *ngFor="let predecessor of dataset.predecessors; let index = index;">
      <p-dropdown *ngIf="availablePredecessors && availablePredecessors.length"
              [(ngModel)]="dataset.predecessors[index].id"
              [ngModelOptions]="{ standalone: true }"
              [options]="availablePredecessors"
              filter="true"
              name="{{ 'predecessor-' + index }}"
              required>
        <ng-template let-datasetItem pTemplate="item">
          <ng-container *ngIf="datasetItem.dataset; else nullDatasetItem;">
            <div>{{ datasetItem.dataset.prefLabel | lang }}</div>
          </ng-container>
          <ng-template #nullDatasetItem>
            {{ datasetItem.label }}
          </ng-template>
        </ng-template>
      </p-dropdown>
      <button (click)="removePredecessor(predecessor)"
              type="button"
              class="btn btn-default btn-sm">
        <i class="fa fa-times" aria-hidden="true"></i>
        {{ 'remove' | translate }}
      </button>
    </li>
  </ul>
</ng-container>
<ng-template #noDatasetRelations>
  <p translate="noDatasetRelations"></p>
</ng-template>
<dataset-relation-type-dropdown
  (onSelectType)="addNewDatasetRelation($event)">
</dataset-relation-type-dropdown>
`
})
export class DatasetRelationsEditComponent implements OnInit {

  @Input() dataset: Dataset

  allDatasets: Dataset[]
  availablePredecessors: DatasetSelectItem[]

  constructor(
    private datasetService: EditorDatasetService,
    private langPipe: LangPipe,
    private translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.getAllDatasets()
  }

  private getAllDatasets() {
    this.datasetService.getAll()
      .subscribe(datasets => {
        this.allDatasets = datasets
        this.refreshAvailablePredecessors()
      })
  }

  private refreshAvailablePredecessors() {
    this.availablePredecessors = []

    this.translateService.get('datasetRelations.noDataset')
      .subscribe(noDatasetLabel => {
        this.availablePredecessors.push({
          label: noDatasetLabel,
          value: null,
          dataset: null
        })

        this.allDatasets.forEach((dataset: Dataset) => {
          if (this.dataset.id === dataset.id) {
            // Dataset cannot have relation with itself
          }
          else {
            this.availablePredecessors.push({
              label: this.langPipe.transform(dataset.prefLabel),
              value: dataset.id,
              dataset: dataset
            })
          }
        })
      })
  }

  addNewDatasetRelation(type: DatasetRelationType) {
    if (type === DatasetRelationType.PREDECESSOR) {
      this.dataset.predecessors = [ ...this.dataset.predecessors, this.datasetService.initNew() ]
    }
    else {
      throw new Error(`Unhandled DatasetRelationType '${type}'`)
    }
  }

  removePredecessor(predecessor: Dataset) {
    const index = this.dataset.predecessors.indexOf(predecessor)
    if (index !== -1) {
      this.dataset.predecessors.splice(index, 1)
    }
  }

}
