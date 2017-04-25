import { Component, Input } from '@angular/core';

import { Organization } from '../model/organization';

@Component({
    selector: 'organization',
    template: `<app-property-value [props]="organization.properties" [key]="'prefLabel'" [lang]="'fi'"></app-property-value>
(<app-property-value [props]="organization.properties" [key]="'abbreviation'" [lang]="'fi'"></app-property-value>)
`
})
export class OrganizationComponent {
    @Input() organization: Organization;
}
