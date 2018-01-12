import { Node } from './node'
import { System } from './system'
import { SystemRole } from './system-role'

export interface SystemInRole extends Node {
  
  system: System
  systemRole: SystemRole
}