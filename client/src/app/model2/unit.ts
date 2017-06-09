import { LangValues } from './lang-values'
import { Node } from './node'

export interface Unit extends Node {
  prefLabel: LangValues
  symbol: LangValues
}
