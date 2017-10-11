import { Node } from './node'
import { LangValues } from './lang-values';

export interface Link extends Node {
    prefLabel: LangValues
    linkUrl: LangValues
}

