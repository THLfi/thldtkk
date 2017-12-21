import {Concept} from './concept';
import {ConfidentialityClass} from './confidentiality-class'
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
import {SecurityClassification} from './security-classification'
import {StudyGroup} from './study-group'
import {UnitType} from './unit-type';
import {Universe} from './universe'
import {UsageCondition} from './usage-condition';
import {UserInformation} from './user-information';

export interface Study extends Node {

  lastModifiedDate?: Date
  published?: boolean
  prefLabel: LangValues
  altLabel: LangValues
  abbreviation: LangValues
  description: LangValues
  usageConditionAdditionalInformation: LangValues
  referencePeriodStart?: string
  referencePeriodEnd?: string
  collectionStartDate?: string
  collectionEndDate?: string
  numberOfObservationUnits?: string
  freeConcepts: LangValues
  personRegistry?: boolean
  registryPolicy: LangValues
  purposeOfPersonRegistry: LangValues
  personRegistrySources: LangValues
  personRegisterDataTransfers: LangValues
  personRegisterDataTransfersOutsideEuOrEea: LangValues
  confidentialityClass?: ConfidentialityClass
  groundsForConfidentiality: LangValues
  securityClassification?: SecurityClassification,
  dataProcessingStartDate?: string
  dataProcessingEndDate?: string
  retentionPeriod?: LangValues
  groundsForRetention?: LangValues
  comment?: string

  lastModifiedByUser?: UserInformation
  ownerOrganization?: Organization
  ownerOrganizationUnit?: OrganizationUnit
  personInRoles: PersonInRole[]
  links: Link[]
  usageCondition?: UsageCondition
  universe?: Universe
  population?: Population
  unitType?: UnitType
  lifecyclePhase?: LifecyclePhase
  conceptsFromScheme: Concept[]
  datasetTypes: DatasetType[]
  studyGroup?: StudyGroup
  datasets: Dataset[]
  predecessors: Study[]
  successors: Study[]

}
