import {Concept} from './concept';
import {ConfidentialityClass} from './confidentiality-class'
import {Dataset} from './dataset'
import {DatasetType} from './dataset-type'
import {LangValues} from './lang-values';
import {LifecyclePhase} from './lifecycle-phase';
import {LegalBasisForHandlingPersonalData} from './legal-basis-for-handling-personal-data';
import {LegalBasisForHandlingSensitivePersonalData} from './legal-basis-for-handling-sensitive-personal-data';
import {TypeOfSensitivePersonalData} from './type-of-sensitive-personal-data';
import {Link} from './link';
import {Node} from './node'
import {Organization} from './organization';
import {OrganizationUnit} from './organization-unit';
import {PersonInRole} from './person-in-role'
import {AssociatedOrganization} from './associated-organization'
import {Population} from './population';
import {PrincipleForPhysicalSecurity} from './principle-for-physical-security'
import {PrincipleForDigitalSecurity} from './principle-for-digital-security'
import {SecurityClassification} from './security-classification'
import {SystemInRole} from './system-in-role'
import {RetentionPolicy} from './retention-policy'
import {ExistenceForm} from './existence-form'
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
  studyType?: string
  referencePeriodStart?: string
  referencePeriodEnd?: string
  collectionStartDate?: string
  collectionEndDate?: string
  numberOfObservationUnits?: string
  freeConcepts: LangValues
  personRegistry?: boolean
  registryPolicy: LangValues
  purposeOfPersonRegistry: LangValues
  usageOfPersonalInformation: LangValues
  personRegistrySources: LangValues
  personRegisterDataTransfers: LangValues
  personRegisterDataTransfersOutsideEuOrEea: LangValues
  legalBasisForHandlingPersonalData: LegalBasisForHandlingPersonalData[]
  otherLegalBasisForHandlingPersonalData?: LangValues
  containsSensitivePersonalData?: boolean,
  legalBasisForHandlingSensitivePersonalData: LegalBasisForHandlingSensitivePersonalData[]
  otherLegalBasisForHandlingSensitivePersonalData?: LangValues
  typeOfSensitivePersonalData: TypeOfSensitivePersonalData[]
  otherTypeOfSensitivePersonalData?: LangValues
  partiesAndSharingOfResponsibilityInCollaborativeStudy?: LangValues
  organizationsAreJointControllers?: boolean
  studyPerformers?: LangValues
  isScientificStudy?: boolean
  profilingAndAutomation?: boolean
  profilingAndAutomationDescription?: LangValues

  // Data security
  confidentialityClass?: ConfidentialityClass
  groundsForConfidentiality: LangValues
  securityClassification?: SecurityClassification,
  principlesForPhysicalSecurity: PrincipleForPhysicalSecurity[]
  principlesForDigitalSecurity: PrincipleForDigitalSecurity[]

  // Archiving
  dataProcessingStartDate?: string
  dataProcessingEndDate?: string
  retentionPolicy?: RetentionPolicy
  retentionPeriod?: LangValues
  groundsForRetention?: LangValues
  nationalArchivesFinlandArchivalDecision?: LangValues
  existenceForms: ExistenceForm[]
  systemInRoles: SystemInRole[]
  physicalLocation?: LangValues
  comment?: string

  lastModifiedByUser?: UserInformation
  ownerOrganization?: Organization
  ownerOrganizationUnit?: OrganizationUnit
  personInRoles: PersonInRole[]
  associatedOrganizations: AssociatedOrganization[]
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
  changedAfterPublish?: boolean

}
