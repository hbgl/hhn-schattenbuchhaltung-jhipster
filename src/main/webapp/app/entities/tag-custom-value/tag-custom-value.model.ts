import { ITag } from 'app/entities/tag/tag.model';
import { ITagCustomType } from 'app/entities/tag-custom-type/tag-custom-type.model';

export interface ITagCustomValue {
  id?: number;
  value?: string;
  tags?: ITag[] | null;
  type?: ITagCustomType;
}

export class TagCustomValue implements ITagCustomValue {
  constructor(public id?: number, public value?: string, public tags?: ITag[] | null, public type?: ITagCustomType) {}
}

export function getTagCustomValueIdentifier(tagCustomValue: ITagCustomValue): number | undefined {
  return tagCustomValue.id;
}
