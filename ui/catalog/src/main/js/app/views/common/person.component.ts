import { Component, Input } from '@angular/core';

import { Person } from '../../model/person';

@Component({
    selector: 'person',
    template: `<property-value [props]="person.properties" [key]="'firstName'"></property-value>
<property-value [props]="person.properties" [key]="'lastName'"></property-value>
`
})
export class PersonComponent {
    @Input() person: Person;
}

