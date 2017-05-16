import { Component, Input } from '@angular/core';

import { OrganizationUnit } from '../../model/organization-unit';

@Component({
    selector: 'organization-unit',
    template: `<property-value [props]="organizationUnit.properties" [key]="'prefLabel'"></property-value>
(<property-value [props]="organizationUnit.properties" [key]="'abbreviation'"></property-value>)`
})
export class OrganizationUnitComponent {
    @Input() organizationUnit: OrganizationUnit;
}

