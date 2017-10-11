import { Node } from './node'
import { LangValues } from './lang-values';

export interface Population extends Node {
    prefLabel: LangValues
    sampleSize: LangValues
    loss: LangValues
    geographicalCoverage: LangValues
}
