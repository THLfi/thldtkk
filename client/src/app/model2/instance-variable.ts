import { Concept } from './concept';
import { LangValues } from './lang-values';

export class InstanceVariable {
  id: string
  prefLabel: LangValues
  description: LangValues
  referencePeriodStart: string
  referencePeriodEnd: string
  technicalName: string
  conceptsFromScheme: Concept[]
}
