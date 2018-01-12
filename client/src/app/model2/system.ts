import { Node } from './node'
import { Organization } from './organization'
import { LangValues } from './lang-values'
import { Link } from './link'

export interface System extends Node {
  
  prefLabel: LangValues
  link?: Link
  ownerOrganization: Organization
}