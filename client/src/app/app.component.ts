import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { GrowlMessageService } from "./services2/growl-message.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(
    public growlMessageService: GrowlMessageService,
    translateService: TranslateService
  ) {
    translateService.setDefaultLang('fi');
    translateService.use('fi');
  }

}
