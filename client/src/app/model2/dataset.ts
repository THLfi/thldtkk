import {InstanceVariable} from './instance-variable';
import {LangValues} from './lang-values';
import {LifecyclePhase} from './lifecycle-phase';
import {Node} from './node'
import {Organization} from './organization';
import {OrganizationUnit} from './organization-unit';
import {Population} from './population';
import {UsageCondition} from './usage-condition';

export interface Dataset extends Node {

  prefLabel: LangValues

  altLabel: LangValues

  abbreviation: LangValues

  description: LangValues

  registryPolicy: LangValues

  researchProjectURL: LangValues

  usageConditionAdditionalInformation: LangValues

  published: boolean

  referencePeriodStart: LangValues

  referencePeriodEnd: LangValues

  owner: Organization

  ownerOrganizationUnit: OrganizationUnit[]

  usageCondition: UsageCondition

  lifecyclePhase: LifecyclePhase

  population: Population

  instanceVariables: InstanceVariable[]

  comment: string

}
