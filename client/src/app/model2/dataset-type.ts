import { Node } from './node'
import { LangValues } from './lang-values';
import { SelectItem } from 'primeng/primeng';

export interface DatasetType extends Node{
  id: string
  prefLabel: LangValues
}
