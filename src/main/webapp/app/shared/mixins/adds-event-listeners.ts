/* eslint-disable @typescript-eslint/explicit-function-return-type */
import { OnDestroy } from '@angular/core';
import { Constructor } from './constructor';

export class ListenerEntry {
  constructor(
    public target: EventTarget,
    public type: string,
    public listener: EventListenerOrEventListenerObject,
    public options?: boolean | AddEventListenerOptions
  ) {}
}

export function AddsEventListeners<TBase extends Constructor<any>>(Base: TBase) {
  return class MixinAddsEventListeners extends Base implements OnDestroy {
    protected addsEventListenerEntries = new Set<ListenerEntry>();

    constructor(...args: any[]) {
      super(...args);
    }

    ngOnDestroy(): void {
      if (typeof super.ngOnDestroy === 'function') {
        super.ngOnDestroy();
      }
      for (const entry of this.addsEventListenerEntries) {
        entry.target.removeEventListener(entry.type, entry.listener, entry.options);
      }
    }

    addEventListener<K extends keyof WindowEventMap>(
      target: EventTarget,
      type: K,
      listener: (this: Window, ev: WindowEventMap[K]) => any,
      options?: boolean | AddEventListenerOptions
    ): boolean;
    addEventListener(
      target: EventTarget,
      type: string,
      listener: EventListenerOrEventListenerObject,
      options?: boolean | AddEventListenerOptions
    ): boolean {
      const entry = new ListenerEntry(target, type, listener, options);
      if (this.addsEventListenerEntries.has(entry)) {
        return false;
      }
      this.addsEventListenerEntries.add(entry);
      entry.target.addEventListener(entry.type, entry.listener, entry.options);
      return true;
    }

    removeEventListener(entry: ListenerEntry): boolean {
      entry.target.removeEventListener(entry.type, entry.listener, entry.options);
      return this.addsEventListenerEntries.delete(entry);
    }
  };
}
