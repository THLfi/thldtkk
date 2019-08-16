import {StudyFormType} from './study-form-type';
import {Node} from './node';
import {OrganizationUnit} from './organization-unit';

export interface StudyForm extends Node {
  type: StudyFormType;
  unitInCharge: OrganizationUnit;
}
