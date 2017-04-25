import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-property-value',
  template: '{{value}}'
})
export class PropertyValue implements OnInit {

  value: string;

  @Input() key: string;
  @Input() lang: string = '';
  @Input() props: { [key: string]: { lang: string, value: string }[]; };

  ngOnInit(): void {
    let values = this.props[this.key] || [];

    if (values.length > 0) {
      let localized = values.filter(e => e.lang == this.lang);
      this.value = localized.length > 0 ?
        localized.map(e => e.value).join(", ") :
        values.map(e => e.value + " (" + e.lang + ")").join(", ");
    } else {
      this.value = "<" + this.key + "@" + this.lang + ">";
    }
  }

}
