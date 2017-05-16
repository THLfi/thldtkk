import { Node } from './node'
import { LangValues } from './lang-values';

export class Population extends Node {
  prefLabel: LangValues
  description: LangValues
  sampleSize: LangValues
  loss: LangValues
  geographicalCoverage: LangValues
}
