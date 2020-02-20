import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core'
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms'

import { GrowlMessageService } from '../../../services-common/growl-message.service'
import { OrganizationUnit } from '../../../model2/organization-unit'
import { TranslateService } from '@ngx-translate/core';
import {Role} from '../../../model2/role';
import {Person} from '../../../model2/person';
import {NodeUtils} from '../../../utils/node-utils';
import {RoleService} from '../../../services-common/role.service';
import {PersonService} from '../../../services-common/person.service';
import {RoleAssociation} from '../../../model2/role-association';
import {Organization} from '../../../model2/organization';

@Component({
  selector: 'organization-unit-edit-modal',
  templateUrl: './organization-unit-edit-modal.component.html'
})
export class OrganizationUnitEditModalComponent implements OnInit {
  @Input() organizationUnit: OrganizationUnit;
  @Input() parentOrganization: Organization;

  form: FormGroup;
  showErrors = false;

  language: string;
  roles: Role[];
  people: Person[];

  saveInProgress = false;

  @Output() onSave: EventEmitter<OrganizationUnit> = new EventEmitter<OrganizationUnit>();
  @Output() onCancel: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    public nodeUtils: NodeUtils,
    private growlMessageService: GrowlMessageService,
    private translateService: TranslateService,
    private roleService: RoleService,
    private personService: PersonService,
    private formBuilder: FormBuilder
  ) {
    this.language = translateService.currentLang;

    this.roleService
      .getAllByAssociation(RoleAssociation.ORGANIZATION_UNIT)
      .subscribe(roles => this.roles = roles);

    this.personService
      .getAll()
      .subscribe(people => {
        people.sort((one, two) => one.firstName.localeCompare(two.firstName))
        this.people = people
      });
  }

  ngOnInit() {
    this.form = this.formBuilder.group({
      prefLabel: [this.organizationUnit.prefLabel[this.language], Validators.required],
      abbreviation: [this.organizationUnit.abbreviation[this.language]],
      personInRoles: this.formBuilder.array(this.organizationUnit.personInRoles
        .map(personInRole => this.formBuilder.group({
          person: [personInRole.person, Validators.required],
          role: [personInRole.role, Validators.required]
        }))
      )
    });
  }

  get prefLabel() {
    return this.form.get('prefLabel');
  }

  get abbreviation() {
    return this.form.get('abbreviation');
  }

  get personInRoles() {
    return this.form.get('personInRoles') as FormArray;
  }

  hasVisibleError(control: AbstractControl) {
    return this.showErrors && control.invalid;
  }

  addPersonInRole() {
    this.personInRoles.push(this.formBuilder.group({
      person: ['', Validators.required],
      role: ['', Validators.required]
    }));
  }

  onSubmit() {
    this.saveInProgress = true;

    if (this.form.invalid) {
      this.showErrors = true;
      this.growlMessageService.showCommonSaveFailedMessage();
      this.saveInProgress = false;
      return;
    }

    this.showErrors = false;

    const values = this.form.value;
    this.organizationUnit.prefLabel[this.language] = values.prefLabel;
    this.organizationUnit.abbreviation[this.language] = values.abbreviation;
    this.organizationUnit.personInRoles = values.personInRoles;

    this.organizationUnit.parentOrganizationId = this.parentOrganization.id;

    this.onSave.emit(this.organizationUnit);
  }
}
