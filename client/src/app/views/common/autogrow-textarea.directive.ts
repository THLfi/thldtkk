import { Directive, ElementRef, AfterViewInit } from '@angular/core';

@Directive({
  selector: '[thlAutogrow]'
})
export class AutogrowTextarea implements AfterViewInit {

  constructor(
    private elementRef: ElementRef
  ) { }

  ngAfterViewInit(): void {
    this.elementRef.nativeElement.addEventListener('input', (e) => {
      this.adjustHeight(this.elementRef, true)
    })
    this.adjustHeight(this.elementRef, false)
  }

  private adjustHeight(elementRef: ElementRef, forceAdjust: boolean) {
    const element = elementRef.nativeElement
    if (element.value || forceAdjust) {
      element.style.height = 'auto'
      element.style.height = element.scrollHeight + 'px'
    }
  }

}
