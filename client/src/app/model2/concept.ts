import { ConceptScheme } from './concept-scheme';
import { LangValues } from './lang-values';
import { Node } from './node'

export interface Concept extends Node {
  prefLabel: LangValues
  conceptScheme: ConceptScheme
}
