import { Node } from './node'
import { LangValues } from './lang-values';

export interface OrganizationUnit extends Node {
  parentOrganizationId: String
  prefLabel: LangValues
  abbreviation: LangValues
}
