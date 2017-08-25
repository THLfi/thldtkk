import { Node } from './node'
import { Person } from './person'
import { Role } from './role'

export interface PersonInRole extends Node {
  person: Person
  role: Role
  public: boolean
}
