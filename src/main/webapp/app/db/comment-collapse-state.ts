export class CommentCollapseState {
  constructor(public id: number, public collapsed: boolean) {}

  public equals(other: CommentCollapseState): boolean {
    if (this === other) {
      return true;
    }
    return this.id === other.id && this.collapsed === other.collapsed;
  }
}
