import { Component, Input } from '@angular/core';

import { PersonInRole } from '../../model/person-in-role';

@Component({
    selector: 'person-in-role',
    template: `<property-value [props]="personInRole.properties" [key]="'prefLabel'"></property-value>`
})
export class PersonInRoleComponent {
    @Input() personInRole: PersonInRole;
}


