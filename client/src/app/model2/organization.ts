import { Node } from './node'
import { LangValues } from './lang-values';
import { OrganizationUnit } from './organization-unit'

export interface Organization extends Node {
  prefLabel: LangValues
  abbreviation: LangValues
  organizationUnit: OrganizationUnit[]
}
