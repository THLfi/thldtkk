import { Directive, Input, forwardRef } from '@angular/core'
import { NG_VALIDATORS, FormControl } from '@angular/forms'
import { Study } from '../../../../model2/study'

@Directive({
  selector: '[validateOrganizationConsistency][ngModel]',
  providers: [
    { provide: NG_VALIDATORS, useExisting: forwardRef(() => OrganizationConsistencyValidator), multi: true }
  ]
})

export class OrganizationConsistencyValidator {
  
  @Input() study: Study
  
  validate(control: FormControl) {

    let selectedStudyOrganizationId: string = control && control.value && control.value.id ? control.value.id : null

    return (this.areStudyAndSystemOrganizationsConsistent(selectedStudyOrganizationId) ? null : {  
      inconsistentStudyAndSystemOrganizations: {
        invalid: true
    }})
  }

  areStudyAndSystemOrganizationsConsistent(studyOrganizationId: string): boolean {
    let organizationsConsistent: boolean = false

    if(this.study && this.study.systemInRoles.length) {
      let systemOrganizationConsistencies: boolean[] =
        this.study.systemInRoles
          .map(systemInRole => systemInRole.system)
          .map(system => system.ownerOrganization)
          .map(organization => organization.id)
          .map(systemOrganizationId => systemOrganizationId == studyOrganizationId) 

      organizationsConsistent = systemOrganizationConsistencies.indexOf(false) == -1        
    }
    else {
      organizationsConsistent = true
    }

    return organizationsConsistent
  }

}