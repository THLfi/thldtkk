import { LangValues } from './lang-values'
import { Node } from './node'

export interface Universe extends Node{
  prefLabel: LangValues
  description: LangValues
}
