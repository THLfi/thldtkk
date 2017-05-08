import { Injectable } from "@angular/core";
import { NodeId } from "../model/node-id";

@Injectable()
export class NodeUtils {
  equals(one: NodeId, two: NodeId): boolean {
    if ((one === null || one === undefined) && (two === null || two === undefined)) {
      return true
    }
    else {
      return one && two ? one.id === two.id : one === two;
    }
  }
}
