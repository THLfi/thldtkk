import { Pipe, PipeTransform } from "@angular/core";
import { LangValues } from "../model/lang-values";
import { TranslateService } from "@ngx-translate/core";

@Pipe({
  name: 'lang'
})
export class LangPipe implements PipeTransform {

  constructor(
    private translate: TranslateService
  ) { }

  transform(langValues: LangValues): any {
    const lang = this.translate.currentLang
    if (langValues[lang]) {
      return langValues[lang]
    }
    else {
      return ''
    }
  }

}
