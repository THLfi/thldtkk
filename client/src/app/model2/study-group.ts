import { LangValues } from './lang-values'
import { Node } from './node'
import { Organization } from './organization'

export interface StudyGroup extends Node {

  prefLabel: LangValues

  description?: LangValues

  ownerOrganization: Organization

  referencePeriodStart?: string

  referencePeriodEnd?: string

}
