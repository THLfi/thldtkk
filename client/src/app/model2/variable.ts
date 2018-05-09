import { LangValues } from './lang-values';
import { Node } from './node'

export interface Variable extends Node {
  prefLabel: LangValues
  description: LangValues
}
