import { Node } from './node'
import { LangValues } from './lang-values';
import { OrganizationUnit } from './organization-unit'
import {OrganizationPersonInRole} from './organization-person-in-role';

export interface Organization extends Node {
  prefLabel: LangValues
  abbreviation: LangValues
  organizationUnit: OrganizationUnit[]
  phoneNumberForRegistryPolicy: string
  addressForRegistryPolicy: LangValues
  personInRoles: OrganizationPersonInRole[];
}
