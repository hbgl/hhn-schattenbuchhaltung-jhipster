export class User {
  constructor(
    public id: string,
    public firstName: string | null,
    public lastName: string | null,
    public email: string | null,
    public imageUrl: string | null
  ) {}

  public get displayText(): string {
    if (this.firstName !== null && this.lastName != null) {
      return `${this.firstName} ${this.lastName}`;
    } else if (this.email !== null) {
      return this.email;
    } else {
      return this.id;
    }
  }
}
