import { LangValues } from './lang-values'
import { Node } from './node'

export interface CodeList extends Node {
  prefLabel: LangValues
  description: LangValues
  owner: LangValues
  referenceId: string
}
