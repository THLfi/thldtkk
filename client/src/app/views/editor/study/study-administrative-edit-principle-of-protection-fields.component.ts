import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from "@angular/core";
import {Study} from "../../../model2/study";
import {MultiSelect, SelectItem} from "primeng/primeng";
import {PrincipleForPhysicalSecurity} from "../../../model2/principle-for-physical-security";
import {PrincipleForDigitalSecurity} from "../../../model2/principle-for-digital-security";
import {TranslateService} from "@ngx-translate/core";
import {LangPipe} from "../../../utils/lang.pipe";
import {SecurityPrincipleService} from "../../../services-common/security-principle.service";
import {SupplementaryPhysicalSecurityPrinciple} from "../../../model2/supplementary-physical-security-principle";
import {SupplementaryDigitalSecurityPrinciple} from "../../../model2/supplementary-digital-security-principle";

@Component({
  selector: 'study-administrative-edit-principle-of-protection-fields',
  templateUrl: './study-administrative-edit-principle-of-protection-fields.component.html',
  styleUrls: ['./study-administrative-edit-principle-of-protection-fields.component.css'],
  providers: [LangPipe]
})
export class StudyAdministrativeEditPrincipleOfProtectionFieldsComponent implements OnInit, OnChanges {
  @Input() study: Study;

  principlesForPhysicalSecurityItems: SelectItem[] = []
  principlesForDigitalSecurityItems: SelectItem[] = []

  private _selectedOtherPhysicalSecurityPrinciples: string[]
  get selectedOtherPhysicalSecurityPrinciples() {
    return this._selectedOtherPhysicalSecurityPrinciples;
  }
  set selectedOtherPhysicalSecurityPrinciples(ids: string[]) {
    this._selectedOtherPhysicalSecurityPrinciples = ids;
    this.study.otherPrinciplesForPhysicalSecurity = ids.map(id => {
      return {id: id, prefLabel: {}}
    })
  }

  otherPhysicalSecurityPrinciples: SelectItem[];

  private _selectedOtherDigitalSecurityPrinciples: string[];
  get selectedOtherDigitalSecurityPrinciples() {
    return this._selectedOtherDigitalSecurityPrinciples;
  }
  set selectedOtherDigitalSecurityPrinciples(ids: string[]) {
    this._selectedOtherDigitalSecurityPrinciples = ids;
    this.study.otherPrinciplesForDigitalSecurity = ids.map(id => {
      return {id: id, prefLabel: {}}
    })
  }

  otherDigitalSecurityPrinciples: SelectItem[];

  newSupplementaryPhysicalSecurityPrinciple: SupplementaryPhysicalSecurityPrinciple;
  newSupplementaryDigitalSecurityPrinciple: SupplementaryDigitalSecurityPrinciple;

  constructor(
    private translateService: TranslateService,
    private securityPrincipleService: SecurityPrincipleService,
    private langPipe: LangPipe
  ) {}

  ngOnInit() {
    this.translateService.get('principlesForPhysicalSecurity')
      .subscribe(translations => {
        this.principlesForPhysicalSecurityItems = Object.keys(PrincipleForPhysicalSecurity)
          .map(key => {
            return { label: translations[key], value: key }
          })
      })

    this.translateService.get('principlesForDigitalSecurity')
      .subscribe(translations => {
        this.principlesForDigitalSecurityItems = Object.keys(PrincipleForDigitalSecurity)
          .map(key => {
            return { label: translations[key], value: key }
          })
      })

    this.getAllSupplementaryPhysicalSecurityPrinciples();
    this.getAllSupplementaryDigitalSecurityPrinciples();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.selectedOtherPhysicalSecurityPrinciples = this.study.otherPrinciplesForPhysicalSecurity.map(principal => principal.id);
    this.selectedOtherDigitalSecurityPrinciples = this.study.otherPrinciplesForDigitalSecurity.map(principal => principal.id);
  }

  private getAllSupplementaryPhysicalSecurityPrinciples() {
    this.otherPhysicalSecurityPrinciples = null;

    this.securityPrincipleService.getAllSupplementaryPhysicalSecurityPrinciples().subscribe(physicalSecurityPrinciples => {
      this.otherPhysicalSecurityPrinciples = physicalSecurityPrinciples.map(principle => {
        return {
          label: this.langPipe.transform(principle.prefLabel),
          value: principle.id
        }
      })
    })
  }

  private getAllSupplementaryDigitalSecurityPrinciples(): void {
    this.otherDigitalSecurityPrinciples = null;

    this.securityPrincipleService.getAllSupplementaryDigitalSecurityPrinciples().subscribe(digitalSecurityPrinciples => {
      this.otherDigitalSecurityPrinciples = digitalSecurityPrinciples.map(principle => {
        return {
          label: this.langPipe.transform((principle.prefLabel)),
          value: principle.id
        }
      })
    })
  }

  showAddPhysicalSecurityPrincipleModal(): void {
    this.newSupplementaryPhysicalSecurityPrinciple = this.securityPrincipleService.initNewSupplementaryPhysicalSecurityPrinciple()
  }

  showAddDigitalSecurityPrincipleModal(): void {
    this.newSupplementaryDigitalSecurityPrinciple = this.securityPrincipleService.initNewSupplementaryDigitalSecurityPrinciple()
  }

  savePhysicalSecurityPrinciple(event): void {
    this.securityPrincipleService.saveSupplementarySecurityPrinciple(this.newSupplementaryPhysicalSecurityPrinciple)
      .subscribe(savedSecurityPrinciple => {
        this.getAllSupplementaryPhysicalSecurityPrinciples()
        this.selectedOtherPhysicalSecurityPrinciples = [...this.selectedOtherPhysicalSecurityPrinciples, savedSecurityPrinciple.id];
        this.closeAddPhysicalSecurityPrincipleModal()
      })
  }

  saveDigitalSecurityPrinciple(event): void {
    this.securityPrincipleService.saveSupplementarySecurityPrinciple(this.newSupplementaryDigitalSecurityPrinciple)
      .subscribe(savedSecurityPrinciple => {
        this.getAllSupplementaryDigitalSecurityPrinciples()
        this.selectedOtherDigitalSecurityPrinciples = [...this.selectedOtherDigitalSecurityPrinciples, savedSecurityPrinciple.id];
        this.closeAddDigitalSecurityPrincipleModal()
      })
  }

  closeAddPhysicalSecurityPrincipleModal(): void {
    this.newSupplementaryPhysicalSecurityPrinciple = null
  }

  closeAddDigitalSecurityPrincipleModal(): void {
    this.newSupplementaryDigitalSecurityPrinciple = null
  }
}
