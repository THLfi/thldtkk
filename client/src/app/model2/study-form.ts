import {StudyFormType} from './study-form-type';
import {StudyFormTypeSpecifier} from './study-form-type-specifier';
import {StudyFormConfirmationState} from './study-form-confirmation-state';
import {Node} from './node';
import {OrganizationUnit} from './organization-unit';
import { LangValues } from './lang-values';

export interface StudyForm extends Node {
  type: StudyFormType;
  typeSpecifier: StudyFormTypeSpecifier;
  additionalDetails: LangValues;
  retentionPeriod: string;
  disposalDate: string;
  unitInChargeConfirmationState: StudyFormConfirmationState;
  retentionPeriodConfirmationState: StudyFormConfirmationState;
  unitInCharge: OrganizationUnit;
}
