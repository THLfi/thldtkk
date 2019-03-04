import { Component, Input, OnInit } from '@angular/core'
import { SelectItem } from 'primeng/components/common/api'
import { TranslateService } from '@ngx-translate/core'
import { TruncateCharactersPipe } from 'ng2-truncate/dist/truncate-characters.pipe'

import { EditorStudyService } from '../../../../services-editor/editor-study.service'
import { LangPipe } from '../../../../utils/lang.pipe'
import { Study } from '../../../../model2/study'
import { StudyRelationType } from './study-relation-type'

class StudySelectItem implements SelectItem {
  constructor(
    public label: string,
    public value: any,
    public study: Study
  ) { }
}

@Component({
  selector: 'study-relations-edit',
  template: `
<label for="studyRelations" class="field-label">
  {{ 'study.relations.label' | translate }}
  <editor-help-link [helpTextComponent]="studyRelationsHelpText"></editor-help-link>
</label>
<ng-container *ngIf="study.predecessors.length; else noStudyRelations;">
  <p>{{ 'study.relations.predecessors' | translate }}</p>
  <ul *ngIf="availablePredecessors && availablePredecessors.length; else loadingStudies;">
    <li *ngFor="let predecessor of study.predecessors; let index = index;">
      <p-dropdown [(ngModel)]="study.predecessors[index].id"
              [ngModelOptions]="{ standalone: true }"
              [options]="availablePredecessors"
              filter="true"
              name="{{ 'predecessor-' + index }}"
              required>
        <ng-template let-studyItem pTemplate="item">
          <ng-container *ngIf="studyItem.study; else nullStudyItem;">
            <div>{{ studyItem.study.prefLabel | lang }}</div>
          </ng-container>
          <ng-template #nullStudyItem>
            {{ studyItem.label }}
          </ng-template>
        </ng-template>
      </p-dropdown>
      <button (click)="removePredecessor(predecessor)"
              type="button"
              class="btn btn-default btn-sm">
        <fa icon="times"></fa>
        {{ 'remove' | translate }}
      </button>
    </li>
  </ul>
  <ng-template #loadingStudies>
    <p><thl-spinner-inline loadingTextKey="study.loading"></thl-spinner-inline></p>
  </ng-template>
</ng-container>
<ng-template #noStudyRelations>
  <p translate="study.relations.noRelations"></p>
</ng-template>
<study-relation-type-dropdown
  (onSelectType)="addNewStudyRelation($event)">
</study-relation-type-dropdown>
<editor-help-text #studyRelationsHelpText key="editor.study.studyRelations.helpText"></editor-help-text>
`
})
export class StudyRelationsEditComponent implements OnInit {

  @Input() study: Study

  allStudies: Study[]
  availablePredecessors: StudySelectItem[]

  constructor(
    private studyService: EditorStudyService,
    private langPipe: LangPipe,
    private truncatePipe: TruncateCharactersPipe,
    private translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.getAllStudies()
  }

  private getAllStudies() {
    this.studyService.getAll()
      .subscribe(studies => {
        this.allStudies = studies
        this.refreshAvailablePredecessors()
      })
  }

  private refreshAvailablePredecessors() {
    this.availablePredecessors = []

    this.translateService.get('study.relations.noStudy')
      .subscribe(noStudyLabel => {
        this.availablePredecessors.push({
          label: noStudyLabel,
          value: null,
          study: null
        })

        this.allStudies.forEach((study: Study) => {
          if (this.study.id === study.id) {
            // Study cannot have relation with itself
          }
          else {
            this.availablePredecessors.push({
              label: this.truncatePipe.transform(this.langPipe.transform(study.prefLabel), 100),
              value: study.id,
              study: study
            })
          }
        })
      })
  }

  addNewStudyRelation(type: StudyRelationType) {
    if (type === StudyRelationType.PREDECESSOR) {
      this.study.predecessors = [ ...this.study.predecessors, this.studyService.initNew() ]
    }
    else {
      throw new Error(`Unhandled StudyRelationType '${type}'`)
    }
  }

  removePredecessor(predecessor: Study) {
    const index = this.study.predecessors.indexOf(predecessor)
    if (index !== -1) {
      this.study.predecessors.splice(index, 1)
    }
  }

}
