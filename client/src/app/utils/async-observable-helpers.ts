import { defer } from "rxjs/observable/defer";

/*
 *  Create async observable that emits-once and completes
 *  after a JS engine turn.
 * 
 *  Source: https://angular.io/guide/testing
 *  Licensed under CC BY 4.0.
 */

/**
 * 
 * @param data 
 */
 export function asyncData<T>(data: T) {
    return defer(() => Promise.resolve(data));
  }