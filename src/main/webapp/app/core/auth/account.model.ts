export class Account {
  constructor(
    public id: string,
    public activated: boolean,
    public authorities: string[],
    public email: string,
    public firstName: string | null,
    public langKey: string,
    public lastName: string | null,
    public login: string,
    public imageUrl: string | null
  ) {}

  hasAnyAuthority(authorities: string[] | string): boolean {
    if (!Array.isArray(authorities)) {
      authorities = [authorities];
    }
    return this.authorities.some((authority: string) => authorities.includes(authority));
  }
}
