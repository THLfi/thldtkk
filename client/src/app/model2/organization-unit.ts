import { Node } from './node'
import { LangValues } from './lang-values';
import {OrganizationPersonInRole} from './organization-person-in-role';

export interface OrganizationUnit extends Node {
  parentOrganizationId: String;
  prefLabel: LangValues;
  abbreviation: LangValues;
  personInRoles: OrganizationPersonInRole[];
}
