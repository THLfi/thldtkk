import { Injectable } from "@angular/core";
import { NodeId } from "../model/node-id";

@Injectable()
export class NodeUtils {
  equals(one: NodeId, two: NodeId): boolean {
    return one && two ? one.id === two.id : one === two;
  }
}
