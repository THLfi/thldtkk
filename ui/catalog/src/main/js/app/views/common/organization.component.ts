import { Component, Input } from '@angular/core';

import { Organization } from '../../model/organization';

@Component({
    selector: 'organization',
    template: `<property-value [props]="organization.properties" [key]="'prefLabel'"></property-value>
(<property-value [props]="organization.properties" [key]="'abbreviation'"></property-value>)
`
})
export class OrganizationComponent {
    @Input() organization: Organization;
}
