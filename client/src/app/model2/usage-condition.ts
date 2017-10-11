import { Node } from './node'
import { LangValues } from './lang-values';

export interface UsageCondition extends Node {
  prefLabel: LangValues
}