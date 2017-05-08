import { Component, Input } from '@angular/core';

import { LifecyclePhase } from '../../model/lifecycle-phase';

@Component({
    selector: 'lifecyclePhase',
    template: `<h2 translate="lifecyclePhase"></h2><property-value [props]="lifecyclePhase.properties" [key]="'prefLabel'"></property-value>`
})
export class LifecyclePhaseComponent {
    @Input() lifecyclePhase: LifecyclePhase;
}

