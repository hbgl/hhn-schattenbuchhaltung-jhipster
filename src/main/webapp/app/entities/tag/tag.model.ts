import { IUser } from 'app/entities/user/user.model';
import { ITagCustomType } from 'app/entities/tag-custom-type/tag-custom-type.model';
import { ITagCustomValue } from 'app/entities/tag-custom-value/tag-custom-value.model';
import { TagKind } from 'app/entities/enumerations/tag-kind.model';

export interface ITag {
  id?: number;
  type?: TagKind;
  text?: string | null;
  author?: IUser;
  person?: IUser | null;
  customType?: ITagCustomType | null;
  customValue?: ITagCustomValue | null;
}

export class Tag implements ITag {
  constructor(
    public id?: number,
    public type?: TagKind,
    public text?: string | null,
    public author?: IUser,
    public person?: IUser | null,
    public customType?: ITagCustomType | null,
    public customValue?: ITagCustomValue | null
  ) {}
}

export function getTagIdentifier(tag: ITag): number | undefined {
  return tag.id;
}
