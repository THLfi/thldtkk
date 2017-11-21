import { Dataset } from '../../../model2/dataset'
import { InstanceVariable } from '../../../model2/instance-variable'
import { StringUtils } from '../../../utils/string-utils'
import { Study } from '../../../model2/study'

export class InstanceVariableReferencePeriod {

  readonly start: string
  readonly end: string
  readonly source: 'instanceVariable' | 'dataset' | 'study'

  constructor(
    private study: Study,
    private dataset: Dataset,
    private instanceVariable: InstanceVariable
  ) {
    if (this.instanceVariableHasValue()) {
      this.start = instanceVariable.referencePeriodStart
      this.end = instanceVariable.referencePeriodEnd
      this.source = 'instanceVariable'
    }
    else if (this.datasetHasValue()) {
      this.start = dataset.referencePeriodStart
      this.end = dataset.referencePeriodEnd
      this.source = 'dataset'
    }
    else if (this.studyHasValue()) {
      this.start = study.referencePeriodStart
      this.end = study.referencePeriodEnd
      this.source = 'study'
    }
    else {
      this.start = null
      this.end = null
      this.source = null
    }
  }

  hasValue(): boolean {
    return StringUtils.isNotBlank(this.start) || StringUtils.isNotBlank(this.end)
  }

  instanceVariableHasValue(): boolean {
    return this.instanceVariable
      && (StringUtils.isNotBlank(this.instanceVariable.referencePeriodStart) || StringUtils.isNotBlank(this.instanceVariable.referencePeriodEnd))
  }

  datasetHasValue(): boolean {
    return this.dataset
      && (StringUtils.isNotBlank(this.dataset.referencePeriodStart) || StringUtils.isNotBlank(this.dataset.referencePeriodEnd))
  }

  studyHasValue(): boolean {
    return this.study
      && (StringUtils.isNotBlank(this.study.referencePeriodStart) || StringUtils.isNotBlank(this.study.referencePeriodEnd))
  }

}
