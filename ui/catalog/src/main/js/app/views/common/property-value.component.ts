import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LangValue } from '../../model/lang-value';

@Component({
  selector: 'property-value',
  template: '{{value}}'
})
export class PropertyValueComponent implements OnInit {

  value: string;

  @Input() key: string;
  @Input() lang: string;
  @Input() props: { [key: string]: LangValue[]; };

  constructor(translate: TranslateService) {
    this.lang = translate.currentLang;
  }

  ngOnInit(): void {
    let values = this.props[this.key] || [];

    if (values.length > 0) {
      let localized = values.filter(e => e.lang == this.lang);
      this.value = localized.length > 0 ?
        localized.map(e => e.value).join(", ") :
        values.map(e => e.value + " (" + e.lang + ")").join(", ");
    } else {
      this.value = "";
    }
  }

}
