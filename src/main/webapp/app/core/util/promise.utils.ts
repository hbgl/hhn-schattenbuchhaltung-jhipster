import { Observable, Subject } from 'rxjs';

/**
 * Convert a promise to a observable that completes when the promise
 * terminates either successfully or erroneously.
 * @param promise
 * @returns
 */
export function promiseToCompletingObservable(promise: Promise<any>): Observable<void> {
  const subject = new Subject<any>();
  promise.finally(() => subject.complete());
  return subject;
}
