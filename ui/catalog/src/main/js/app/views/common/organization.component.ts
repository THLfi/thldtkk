import { Component, Input } from '@angular/core';

import { Organization } from '../../model/organization';

@Component({
    selector: 'organization',
    template: `<property-value [props]="organization.properties" [key]="'prefLabel'" [lang]="'fi'"></property-value>
(<property-value [props]="organization.properties" [key]="'abbreviation'" [lang]="'fi'"></property-value>)
`
})
export class OrganizationComponent {
    @Input() organization: Organization;
}
