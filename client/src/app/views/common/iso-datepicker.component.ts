import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ControlValueAccessor } from "@angular/forms";
import { DatePipe } from "@angular/common";

@Component({
  selector: 'iso-datepicker',
  templateUrl: './iso-datepicker.component.html'
})
export class IsoDatePicker implements ControlValueAccessor {

  private readonly _isoDateRegex: string = "^\\d{4}-\\d{2}-\\d{2}$"

  public _showDatePicker: boolean = false

  private onChange: any = Function.prototype
  private onTouched: any = Function.prototype

  private _dateAsString: string
  private _date: Date

  @Input()
  get dateAsString(): string {
    return this._dateAsString
  }
  set dateAsString(value: string) {
    this._dateAsString = value
    this._date = this.isValidIsoDate(value) ? new Date(value) : null
  }

  @Output() dateAsStringChange: EventEmitter<string> = new EventEmitter<string>()

  constructor(
    private datePipe: DatePipe
  ) { }

  onDateTextInputChange(event: any): void {
    const value: string = event.target.value
    if (!value || '' == value.trim()) {
      this.dateAsStringChange.emit(null)
    }
    else if (this.isValidIsoDate(value)) {
      this.dateAsStringChange.emit(value)
    }
  }

  private isValidIsoDate(value: string): boolean {
    return new RegExp(this._isoDateRegex).test(value)
  }

  onSelectDateFromCalendarPopup(date: Date): void {
    this.dateAsString = this.datePipe.transform(date, 'yyyy-MM-dd')
    this.dateAsStringChange.emit(this.dateAsString)
    this._showDatePicker = false
  }

  writeValue(value: any): void {
    if (value === undefined || value === null) {
      // Nothing to do
    }
    else if (!(typeof value === 'string')) {
      throw new Error('Model must be a string')
    }
    else if (this.isValidIsoDate(value)) {
      throw new Error('Model string does not match ISO date format "yyyy-mm-dd"')
    }
    else {
      this._dateAsString = value
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn
  }

}
