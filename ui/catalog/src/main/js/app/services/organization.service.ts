import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from "rxjs";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch'

import { Organization } from "../model/organization";

@Injectable()
export class OrganizationService {
    constructor(
        private _http: Http
    ) {}

    getAllOrganizations(): Observable<Organization[]> {
        return this._http.get('../metadata-api/termed/types/Organization/nodes')
            .map(response => response.json() as Organization[]);
    }

    getOrganization(organizationId: String): Observable<Organization> {
        return this.getAllOrganizations()
            .map(organizations => {
                for (let organization of organizations) {
                    if (organizationId === organization.id) {
                        return organization;
                    }
                }
            });
    }

}
