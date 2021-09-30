import { LOADING_MIN_DELAY_MS } from 'app/entities/constants';
import { Observable, ObservableInput, ObservedValueOf, Subject, timer, zip } from 'rxjs';
import { map } from 'rxjs/operators';

export function loading<O extends ObservableInput<any>>(o: O): Observable<ObservedValueOf<O>> {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  return zip(o, timer(LOADING_MIN_DELAY_MS)).pipe(map(v => v[0]));
}

export function toSubject<O extends Observable<any>>(o: O): Subject<ObservedValueOf<O>> {
  const subject = new Subject<ObservedValueOf<O>>();
  o.subscribe(subject);
  return subject;
}
