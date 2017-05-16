import { Node } from './node'
import { LangValues } from "./lang-values";

export interface Dataset extends Node {

  prefLabel: LangValues

  altLabel: LangValues

  abbreviation: LangValues

  description: LangValues

  registryPolicy: LangValues

  researchProjectURL: LangValues

  usageConditionAdditionalInformation: LangValues

  isPublic: boolean

  referencePeriodStart: LangValues

  referencePeriodEnd: LangValues

/*

  Following will be added in future commit.

  owner: any // TODO

  ownerOrganizationUnit: [ any ] // TODO

  usageCondition: any // TODO

  lifecyclePhase: any // TODO

  population: any // TODO

  instanceVariables: any // TODO

*/
}
