import { Concept } from './concept';
import { LangValues } from './lang-values';
import { Quantity } from './quantity';
import { Unit } from './unit';

export interface InstanceVariable {
  id: string
  prefLabel: LangValues
  description: LangValues
  referencePeriodStart: string
  referencePeriodEnd: string
  technicalName: string
  conceptsFromScheme: Concept[]
  freeConcepts: LangValues
  valueDomainType: string
  quantity: Quantity
  unit: Unit
}
