import { Injectable } from '@angular/core';

import { Node } from '../model2/node';

@Injectable()
export class NodeUtils {
  equals(one: Node, two: Node): boolean {
    if ((one === null || one === undefined) && (two === null || two === undefined)) {
      return true
    }
    else {
      return one && two ? one.id === two.id : one === two
    }
  }
}
