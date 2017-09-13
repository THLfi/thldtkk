import {LangValues} from './lang-values';
import { Node } from './node'

export interface UnitType extends Node {
  prefLabel: LangValues
  description: LangValues
}
