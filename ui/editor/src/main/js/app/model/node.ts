import { LangValue } from './lang-value';
import { NodeId } from './node-id';

export interface Node extends NodeId {
    properties: { [key: string]: LangValue[] };
    references: { [key: string]: NodeId[] };
}
