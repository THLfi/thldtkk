import {Concept} from './concept';
import {Dataset} from './dataset'
import {DatasetType} from './dataset-type'
import {LangValues} from './lang-values';
import {LifecyclePhase} from './lifecycle-phase';
import {Link} from './link';
import {Node} from './node'
import {Organization} from './organization';
import {OrganizationUnit} from './organization-unit';
import {PersonInRole} from './person-in-role'
import {Population} from './population';
import {UnitType} from './unit-type';
import {Universe} from './universe'
import {UsageCondition} from './usage-condition';

export interface Study extends Node {

  prefLabel: LangValues

  altLabel: LangValues

  abbreviation: LangValues

  description: LangValues

  registryPolicy: LangValues

  usageConditionAdditionalInformation: LangValues

  published: boolean

  referencePeriodStart: string

  referencePeriodEnd: string

  collectionStartDate: string

  collectionEndDate: string

  ownerOrganization: Organization

  ownerOrganizationUnit: OrganizationUnit[]

  usageCondition: UsageCondition

  lifecyclePhase: LifecyclePhase

  population: Population

  comment: string

  numberOfObservationUnits: string

  conceptsFromScheme: Concept[]

  links: Link[]

  freeConcepts: LangValues

  datasetTypes: DatasetType[]

  unitType: UnitType

  universe: Universe

  personInRoles: PersonInRole[]

  datasets: Dataset[]

  predecessors: Study[]
  
  successors: Study[]

  lastModifiedDate?: Date

}
