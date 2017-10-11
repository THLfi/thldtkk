import { Node } from './node'
import { LangValues } from './lang-values';

export interface OrganizationUnit extends Node {
  prefLabel: LangValues
  abbreviation: LangValues
}
