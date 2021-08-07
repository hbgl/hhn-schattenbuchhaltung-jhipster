import { Type } from 'class-transformer';
import { User } from '../user/user.model';

export class CommentIn {
  constructor(public parentId: number | null, public ledgerEntryNo: string, public contentHtml: string) {}

  public static fromOther(other: CommentIn): CommentIn {
    return new CommentIn(other.parentId, other.ledgerEntryNo, other.contentHtml);
  }
}

export class Comment {
  @Type(() => User)
  public author: User;

  constructor(public id: number, public contentHtml: string, public createdAt: string, public parentId: number | null, author: User) {
    this.author = author;
  }
}
