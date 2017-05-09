import { Injectable } from "@angular/core";

import { Node } from "../model/node";
import { NodeId } from "../model/node-id";

@Injectable()
export class NodeUtils {
  equals(one: NodeId, two: NodeId): boolean {
    if ((one === null || one === undefined) && (two === null || two === undefined)) {
      return true
    }
    else {
      return one && two ? one.id === two.id : one === two
    }
  }

  createNode(): Node {
    return {
      id: null,
      type: {
        id: null,
        graph: {
          id: null
        }
      },
      properties: {},
      references: {}
    }
  }

  initProperties(node: Node, properties: string[]) {
    for (let property of properties) {
      if (!node.properties[property] || !node.properties[property][0]) {
        node.properties[property] = [
          {
            lang: 'fi',
            value: null
          }
        ]
      }
    }
  }
}
