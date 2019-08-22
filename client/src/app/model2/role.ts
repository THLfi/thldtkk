import { LangValues } from './lang-values'
import { Node } from './node'

export interface Role extends Node {
  prefLabel: LangValues;
  associations: string[];
}
