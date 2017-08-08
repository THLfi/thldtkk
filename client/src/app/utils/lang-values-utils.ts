import { LangValues } from '../model2/lang-values'
import { StringUtils } from './string-utils'

export class LangValuesUtils {

  static allValuesAreBlank(values: LangValues): boolean {
    if (values) {
      for (let lang in values) {
        if (StringUtils.isNotBlank(values[lang])) {
          return false
        }
      }
    }
    return true
  }

  static anyValueIsNotBlank(values: LangValues): boolean {
    return !this.allValuesAreBlank(values)
  }

}
