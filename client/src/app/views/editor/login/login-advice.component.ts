import { Component, ViewEncapsulation } from '@angular/core'

@Component({
  selector: 'login-advice',
  template: '<div class="login-advice" pTooltip="{{\'editorLoginComponent.loginAdviceTooltip\' | translate}}" tooltipPosition="bottom" tooltipStyleClass="login-advice-tooltip">{{\'editorLoginComponent.loginAdvice\' | translate}}</div><div class="login-advice tooltip-padding"></div>',
  styleUrls: ['./login-advice.component.css'],
  encapsulation: ViewEncapsulation.None
})

export class LoginAdviceComponent {}