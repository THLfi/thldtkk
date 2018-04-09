import { ActivatedRoute } from '@angular/router'
import { Component, OnInit} from '@angular/core'
import { TranslateService } from '@ngx-translate/core'

import { Study } from '../../../model2/study'
import { LangPipe } from '../../../utils/lang.pipe'
import { Title } from '@angular/platform-browser'
import { StudyGroup } from '../../../model2/study-group'
import { StudyGroupService } from '../../../services-common/study-group.service'

@Component({
    templateUrl: './studygroup-view.component.html',
    styleUrls: ['./studygroup-view.component.css']
})
export class StudyGroupViewComponent implements OnInit {

    language: string

    studyGroup: StudyGroup
    studies: Study[]
    loadingStudies: boolean

    constructor(private route: ActivatedRoute,
                private translateService: TranslateService,
                private studyGroupService: StudyGroupService,
                private titleService: Title,
                private langPipe: LangPipe) {
      this.language = this.translateService.currentLang
    }

    ngOnInit() {
      const studyGroupId: string = this.route.snapshot.params['id']
      this.loadingStudies = true
      this.studyGroupService.get(studyGroupId).subscribe(studyGroup => {
        this.studyGroup = studyGroup
        this.updatePageTitle()
      });
      this.studyGroupService.getStudies(studyGroupId)
        .subscribe(studies => {
          this.studies = studies
          this.loadingStudies = false
      })
    }

    updatePageTitle() {
      if (this.studyGroup.prefLabel) {
        const translatedLabel: string = this.langPipe.transform(this.studyGroup.prefLabel)
        const bareTitle: string = this.titleService.getTitle();
        this.titleService.setTitle(translatedLabel + ' - ' + bareTitle)
      }
    }

}
