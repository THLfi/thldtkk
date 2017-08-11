import { DatePipe } from '@angular/common'
import { Injectable } from '@angular/core'
import { TranslateService } from '@ngx-translate/core'
import { Observable } from 'rxjs'
import { LocaleSettings } from 'primeng/components/calendar/calendar'

@Injectable()
export class DateUtils {

  constructor(
    private datePipe: DatePipe,
    private translateService: TranslateService
  ) { }

  convertToIsoDate(date: Date): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd')
  }

  getLocaleSettings(): Observable<LocaleSettings> {
    return this.translateService.get('pcalendar')
      .map(pcalendar => pcalendar ? pcalendar : {
        firstDayOfWeek: 0,
        dayNames: [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],
        dayNamesShort: [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],
        dayNamesMin: [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ],
        monthNames: [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ],
        monthNamesShort: [ "Jan", "Feb", "Mar", "Apr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ]
      })
  }

}
