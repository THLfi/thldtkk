import {Node} from './node';
import {Person} from './person';
import {Role} from './role';

export interface OrganizationPersonInRole extends Node {
  person: Person;
  role: Role;
}
