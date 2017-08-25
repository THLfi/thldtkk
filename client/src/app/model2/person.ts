import { Node } from './node'

export interface Person extends Node {
  firstName: string
  lastName?: string
  email?: string
  phone?: string
}
