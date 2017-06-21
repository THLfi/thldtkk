import { LangValues } from './lang-values'
import { Node } from './node'

export interface CodeItem extends Node {
  code: string
  prefLabel: LangValues
}
