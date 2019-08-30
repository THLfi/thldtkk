import { LangValues } from './lang-values'
import { Node } from './node'
import { RoleLabel } from './role-label'

export interface Role extends Node {
  label?: RoleLabel;
  prefLabel: LangValues;
  associations: string[];
}
