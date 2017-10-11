import { Node } from './node'
import { LangValues } from './lang-values';

export interface LifecyclePhase extends Node {
  prefLabel: LangValues
}
