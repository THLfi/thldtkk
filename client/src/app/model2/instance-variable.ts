import { CodeList } from './code-list';
import { Concept } from './concept';
import { Dataset } from './dataset';
import { InstanceQuestion } from './instance-question'
import { LangValues } from './lang-values';
import { Quantity } from './quantity';
import { Unit } from './unit';
import { UnitType } from './unit-type';
import { UserInformation } from './user-information';
import { Variable } from './variable'

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
  qualityStatement: LangValues
  codeList: CodeList
  missingValues: LangValues
  defaultMissingValue: string
  valueRangeMin: number
  valueRangeMax: number
  variable: Variable
  partOfGroup: LangValues
  source: Dataset
  sourceDescription: LangValues
  dataType: string
  unitType: UnitType
  dataset: Dataset
  instanceQuestions: InstanceQuestion[],
  dataFormat: LangValues
  lastModifiedByUser?: UserInformation
  published?: boolean
}
