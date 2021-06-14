import { ITagCustomValue } from 'app/entities/tag-custom-value/tag-custom-value.model';
import { ITag } from 'app/entities/tag/tag.model';

export interface ITagCustomType {
  id?: number;
  name?: string;
  enabled?: boolean;
  values?: ITagCustomValue[] | null;
  tags?: ITag[] | null;
}

export class TagCustomType implements ITagCustomType {
  constructor(
    public id?: number,
    public name?: string,
    public enabled?: boolean,
    public values?: ITagCustomValue[] | null,
    public tags?: ITag[] | null
  ) {
    this.enabled = this.enabled ?? false;
  }
}

export function getTagCustomTypeIdentifier(tagCustomType: ITagCustomType): number | undefined {
  return tagCustomType.id;
}
