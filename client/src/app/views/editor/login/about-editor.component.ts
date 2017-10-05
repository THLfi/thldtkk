import { Component, OnInit, ViewEncapsulation } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

@Component({
  selector: 'about-editor',
  template: '<div [innerHTML]="aboutTextHtml" class="about-editor"></div>',
  styleUrls: ['./about-editor.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AboutEditorComponent implements OnInit {

  aboutTextHtml:string
  
  constructor(private translateService: TranslateService) {}

  ngOnInit() {
    this.translateService.get('editorLoginComponent.aboutEditor')
      .subscribe(aboutEditor => this.aboutTextHtml = aboutEditor)
  }

}
