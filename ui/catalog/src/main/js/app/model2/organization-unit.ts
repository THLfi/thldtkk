import { Node } from './node'
import { LangValues } from './lang-values';

export class OrganizationUnit extends Node {
  prefLabel: LangValues
  abbreviation: LangValues
}
