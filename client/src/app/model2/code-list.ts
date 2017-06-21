import { CodeItem } from './code-item'
import { LangValues } from './lang-values'
import { Node } from './node'

export interface CodeList extends Node {
  codeListType: string
  referenceId: string
  prefLabel: LangValues
  description: LangValues
  owner: LangValues
  codeItems: CodeItem[]
}
