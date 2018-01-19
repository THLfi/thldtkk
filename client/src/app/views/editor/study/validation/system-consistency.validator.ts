import { Directive, Input, forwardRef } from '@angular/core'
import { NG_VALIDATORS, FormControl } from '@angular/forms'
import { Study } from '../../../../model2/study'

@Directive({
  selector: '[validateSystemConsistency][ngModel]',
  providers: [
    { provide: NG_VALIDATORS, useExisting: forwardRef(() => SystemConsistencyValidator), multi: true }
  ]
})

export class SystemConsistencyValidator {
  
  @Input() study: Study
  
  validate(control: FormControl) {

    return this.getSystemOrganizationId(control) == this.getStudyOrganizationId() ? null : {  
      inconsistentSystemAndStudyOrganizations: {
        invalid: true
    }}
  }

  getSystemOrganizationId(systemControl: FormControl):string {
    return systemControl && systemControl.value && systemControl.value.ownerOrganization ? systemControl.value.ownerOrganization.id : null

  }

  getStudyOrganizationId(): string {
    return this.study && this.study.ownerOrganization ? this.study.ownerOrganization.id : null
  }
  
}