import {Component, Input, OnInit} from '@angular/core'
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'thl-info-tooltip',
  template: '<span class="glyphicon glyphicon-info-sign" ' +
    'pTooltip="{{text}}" ' +
    '*ngIf="text.length > 0"' +
    'tooltipPosition="bottom" tooltipStyleClass="catalog-helptext-tooltip">' +
    '</span>'
})
export class InfoTooltipComponent implements OnInit {

  @Input() key: string;

  text: string;

  constructor(private translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.translateService.get(this.key).subscribe((res: string) => {
      this.text = res;
    });
  }

}
