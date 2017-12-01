import { Component, forwardRef, OnInit, Input, Output, EventEmitter } from '@angular/core'
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms'

import { CurrentUserService } from '../../services-editor/user.service'
import { Organization } from '../../model2/organization'
import { OrganizationService } from '../../services-common/organization.service'
import { NodeUtils } from '../../utils/node-utils'

const noop = () => { }

export const ORGANIZATION_DROPDOWN_CONTROL_VALUE_ACCESSOR: any = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => OrganizationDropdownComponent),
  multi: true
}

@Component({
  selector: 'organization-dropdown',
  template: `
  <select [(ngModel)]="value"
          (blur)="onBlur()"
          [compareWith]="nodeUtils.equals"
          id="{{ selectId }}"
          class="form-control">
    <option [ngValue]="null"></option>
    <option *ngFor="let organization of availableOrganizations"
            [ngValue]="organization">
      {{ organization.prefLabel | lang }}
      <span *ngIf="organization.abbreviation | lang">
        ({{ organization.abbreviation | lang }})
      </span>
    </option>
  </select>
`,
  providers: [
    ORGANIZATION_DROPDOWN_CONTROL_VALUE_ACCESSOR
  ]
})
export class OrganizationDropdownComponent implements OnInit, ControlValueAccessor {

  @Input() selectId: string

  @Output() onChange: EventEmitter<Organization> = new EventEmitter<Organization>()

  private internalValue: Organization

  availableOrganizations: Organization[] = []

  private onTouchedCallback: () => void = noop
  private onChangeCallback: ( _: any) => void = noop

  constructor(
    private currentUserService: CurrentUserService,
    private organizationService: OrganizationService,
    public nodeUtils: NodeUtils
  ) { }

  get value(): any {
    return this.internalValue
  }

  set value(value: any) {
    if (value !== this.internalValue) {
      this.internalValue = value
      this.onChangeCallback(value)
      this.onChange.emit(this.value)
    }
  }

  ngOnInit(): void {
    this.getAvailableOrganizations()
  }

  private getAvailableOrganizations() {
    this.currentUserService.isUserAdmin()
      .subscribe(isAdmin => {
        if (isAdmin) {
          this.organizationService.getAllOrganizations()
            .subscribe(organizations => {
              this.availableOrganizations = organizations
            })
        }
        else {
          this.currentUserService.getUserOrganizations()
            .subscribe(organizations => {
              this.availableOrganizations = organizations
            })
        }
      })
  }

  onBlur() {
    this.onTouchedCallback()
  }

  writeValue(value: any) {
    if (value !== this.internalValue) {
      this.internalValue = value
    }
  }

  registerOnChange(fn: any) {
    this.onChangeCallback = fn
  }

  registerOnTouched(fn: any) {
    this.onTouchedCallback = fn
  }

}
