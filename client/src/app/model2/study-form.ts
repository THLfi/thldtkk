import {StudyFormType} from "./study-form-type";
import {Node} from "./node";

export interface StudyForm extends Node {
  type: StudyFormType;
}
