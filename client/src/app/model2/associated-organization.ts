import { Node } from './node'
import { Organization } from './organization'

export interface AssociatedOrganization extends Node {
  organization: Organization
  isRegistryOrganization: boolean
}
