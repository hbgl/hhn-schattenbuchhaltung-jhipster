import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { uniqueId } from 'app/core/util/unique-id';

@Pipe({ name: 'uniqueId' })
export class UniqueIdPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string): string {
    const id = uniqueId();
    return `${value}${id}`;
  }
}
