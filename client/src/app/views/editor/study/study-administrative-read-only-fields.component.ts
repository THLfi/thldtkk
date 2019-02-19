import { Component, Input } from '@angular/core'

import { Study } from '../../../model2/study'

@Component({
  selector: 'study-administrative-read-only-fields',
  template: `
<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'registryOrganization' | translate }}</strong></p>
    <p *ngIf="study.ownerOrganization">
      {{ study.ownerOrganization.prefLabel | lang }}
      <span *ngIf="study.ownerOrganization.abbreviation | lang">
        ({{ study.ownerOrganization.abbreviation | lang }})
      </span>
    </p>
  </div>
</div>

<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'registryOrganizationUnit' | translate }}</strong></p>
    <ng-container *ngIf="study.ownerOrganization">
      <p *ngIf="study.ownerOrganizationUnit">
        {{ study.ownerOrganizationUnit.prefLabel | lang }}
        <span *ngIf="study.ownerOrganizationUnit.abbreviation | lang">
          ({{ study.ownerOrganizationUnit.abbreviation | lang }})
        </span>
      </p>
    </ng-container>
  </div>
</div>

<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'addressForRegistryPolicy' | translate }}</strong></p>
    <p *ngIf="study.ownerOrganization" class="preserve-line-breaks">{{ study.ownerOrganization.addressForRegistryPolicy | lang }}</p>
  </div>
</div>

<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'phoneNumberForRegistryPolicy' | translate }}</strong></p>
    <p *ngIf="study.ownerOrganization">{{ study.ownerOrganization.phoneNumberForRegistryPolicy }}</p>
  </div>
</div>

<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'study.personInRolesLabel' | translate }}</strong></p>
    <table *ngIf="study.personInRoles.length; else noStudyPersonInRoles;"
           class="table table-striped table-hover">
      <tr>
        <th translate="personInRoles.person"></th>
        <th translate="personInRoles.role"></th>
        <th translate="personInRoles.public.title"></th>
      </tr>
      <tr *ngFor="let personInRole of study.personInRoles">
        <td>
          {{ personInRole.person.firstName }}
          {{ personInRole.person.lastName }}
          <ng-container *ngIf="personInRole.person.email">
            <br>
            {{ personInRole.person.email }}
          </ng-container>
          <ng-container *ngIf="personInRole.person.phone">
            <br>
            {{ personInRole.person.phone }}
          </ng-container>
        </td>
        <td>
          <ng-container *ngIf="personInRole.role; else noRole;">
            {{ personInRole.role.prefLabel | lang }}
          </ng-container>
          <ng-template #noRole>
            {{ 'noRole' | translate }}
          </ng-template>
        </td>
        <td>
          {{ 'personInRoles.public.' + personInRole.public | translate }}
        </td>
      </tr>
    </table>
    <ng-template #noStudyPersonInRoles>
      <p translate="noStudyPersonInRoles"></p>
    </ng-template>
  </div>
</div>

<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'description' | translate }}</strong></p>
    <p class="preserve-line-breaks">{{ study.description | lang }}</p>
  </div>
</div>

<div *ngIf="study.population"
     class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'population' | translate }}</strong></p>
    <p class="preserve-line-breaks">{{ study.population.prefLabel | lang }}</p>
  </div>
</div>

<div class="row">
  <div class="col-xs-12">
    <p><strong class="field-label">{{ 'referencePeriod' | translate }}</strong></p>
    <p *ngIf="study.referencePeriodStart || study.referencePeriodEnd">
      <ng-container *ngIf="study.referencePeriodStart">
        {{ study.referencePeriodStart | date:'dd.MM.yyyy' }}
      </ng-container>
      —
      <ng-container *ngIf="study.referencePeriodEnd">
        {{ study.referencePeriodEnd | date:'dd.MM.yyyy' }}
      </ng-container>
    </p>
  </div>
</div>
`
})
export class StudyAdministrativeReadOnlyFieldsComponent {
  @Input() study: Study
}
