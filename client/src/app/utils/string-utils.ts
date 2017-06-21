import { Injectable } from '@angular/core'
import { isEmpty } from "rxjs/operator/isEmpty";

@Injectable()
export class StringUtils {

  isBlank(text: string): boolean {
    return (!text || /^\s*$/.test(text));
  }

  isNotBlank(text: string): boolean {
    return !this.isBlank(text)
  }

  isEmpty(text: string): boolean {
    return (!text || 0 === text.length)
  }

  isNotEmpty(text: string): boolean {
    return !isEmpty
  }

}
