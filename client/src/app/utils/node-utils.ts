import { Injectable } from '@angular/core';

import { Node } from '../model2/node';

@Injectable()
export class NodeUtils {

  /**
   * Compare two nodes based on their id attribute.
   * @param one
   * @param two
   * @returns {boolean}
   */
  equals(one: Node, two: Node): boolean {
    if ((one === null || one === undefined) && (two === null || two === undefined)) {
      return true
    }
    else {
      return one && two ? one.id === two.id : one === two
    }
  }

  /**
   *
   * @param node Object which has the properties to initialize.
   * @param properties Properties of type <code>LangValues</code> that will be initialized.
   * @param languages Which languages will be initialized.
   */
  initLangValuesProperties(node: any, properties: string[], languages: string[]): void {
    if (!node) {
      return
    }

    properties.forEach(property => {
      if (!node[property]) {
        node[property] = {}
      }
      languages.forEach(language => {
        if (!node[property][language]) {
          node[property][language] = ''
        }
      })
    })
  }

}
