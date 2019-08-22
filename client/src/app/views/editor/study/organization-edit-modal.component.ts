import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core'
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms'

import {GrowlMessageService} from '../../../services-common/growl-message.service'
import {Organization} from '../../../model2/organization'
import {TranslateService} from '@ngx-translate/core';
import {NodeUtils} from '../../../utils/node-utils';
import {RoleService} from '../../../services-common/role.service';
import {RoleAssociation} from '../../../model2/role-association';
import {Role} from '../../../model2/role';
import {Person} from '../../../model2/person';
import {PersonService} from '../../../services-common/person.service';

@Component({
  selector: 'organization-edit-modal',
  templateUrl: './organization-edit-modal.component.html'
})
export class OrganizationEditModalComponent implements OnInit {
  @Input() organization: Organization;

  form: FormGroup;
  showErrors = false;

  language: string;
  roles: Role[];
  people: Person[];

  @Output() onSave: EventEmitter<Organization> = new EventEmitter<Organization>();
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
      .getAllByAssociation(RoleAssociation.ORGANIZATION)
      .subscribe(roles => this.roles = roles);

    this.personService
      .getAll()
      .subscribe(people => this.people = people);
  }

  ngOnInit() {
    this.form = this.formBuilder.group({
      prefLabel: [this.organization.prefLabel[this.language], Validators.required],
      abbreviation: [this.organization.abbreviation[this.language]],
      personInRoles: this.formBuilder.array(this.organization.personInRoles
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
    if (this.form.invalid) {
      this.showErrors = true;
      this.growlMessageService.buildAndShowMessage(
        'error',
        'operations.common.save.result.fail.summary',
        'operations.common.save.result.fail.detail'
      );

      return;
    }

    this.showErrors = false;

    const values = this.form.value;
    this.organization.prefLabel[this.language] = values.prefLabel;
    this.organization.abbreviation[this.language] = values.abbreviation;
    this.organization.personInRoles = values.personInRoles;

    this.onSave.emit(this.organization);
  }
}
