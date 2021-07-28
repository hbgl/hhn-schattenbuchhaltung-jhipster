import * as internal from 'node:stream';
import { Type } from 'class-transformer';
import { TagKind } from 'app/entities/enumerations/tag-kind.model';

// Had to factor out some properties from the constructor into
// the class body in order to apply the @Type decorator.
// Otherwise it would throw the following compiler error:
// "Unable to resolve signature of parameter decorator when called as an expression.ts(1239)"

export class CostCenter {
  constructor(public no: string, public name: string, public rank: internal) {}

  public get displayText(): string {
    return `${this.no} (${this.name})`;
  }
}

export class Division {
  constructor(public no: string, public name: string) {}

  public get displayText(): string {
    return `${this.no} (${this.name})`;
  }
}

export class CostType {
  constructor(public no: string, public name: string) {}

  public get displayText(): string {
    return `${this.no} (${this.name})`;
  }
}

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

export class Tag {
  constructor(
    public id: number,
    public type: TagKind,
    public text: string | null,
    public person: User | null,
    public customType: string | null,
    public customValue: string | null
  ) {}

  public get displayText(): string {
    if (this.type === 'TEXT') {
      return this.text!;
    }
    if (this.type === 'PERSON') {
      return this.person!.displayText;
    }
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (this.type === 'CUSTOM') {
      return this.customValue!;
    }
    return '';
  }
}

export class Comment {
  @Type(() => User)
  public author: User;

  constructor(public id: number, public contentHtml: string, public createdAt: string, author: User) {
    this.author = author;
  }
}

export class LedgerListEntry {
  // Had to factor out some properties from the constructor into
  // the class body in order to apply the @Type decorator.
  // Otherwise it would throw the following compiler error:
  // "Unable to resolve signature of parameter decorator when called as an expression.ts(1239)"

  @Type(() => CostCenter)
  public costCenter1: CostCenter;

  @Type(() => CostCenter)
  public costCenter2: CostCenter;

  @Type(() => CostCenter)
  public costCenter3: CostCenter;

  @Type(() => Division)
  public division: Division | null;

  @Type(() => CostType)
  public costType: CostType | null;

  constructor(
    public year: string,
    costCenter1: CostCenter,
    costCenter2: CostCenter,
    costCenter3: CostCenter,
    public no: string,
    public description: string,
    public aNo: string | null,
    public bookingDate: string,
    public income: string,
    public expenditure: string,
    public liability: string,
    division: Division | null,
    costType: CostType | null
  ) {
    this.costCenter1 = costCenter1;
    this.costCenter2 = costCenter2;
    this.costCenter3 = costCenter3;
    this.division = division;
    this.costType = costType;
  }
}

export class LedgerEntryDetail {
  @Type(() => CostCenter)
  public costCenter1: CostCenter;

  @Type(() => CostCenter)
  public costCenter2: CostCenter;

  @Type(() => CostCenter)
  public costCenter3: CostCenter;

  @Type(() => Division)
  public division: Division | null;

  @Type(() => CostType)
  public costType: CostType | null;

  @Type(() => Tag)
  public tags: Tag[];

  @Type(() => Comment)
  public comments: Comment[];

  constructor(
    public year: string,
    costCenter1: CostCenter,
    costCenter2: CostCenter,
    costCenter3: CostCenter,
    public no: string,
    public description: string,
    public aNo: string | null,
    public bookingDate: string,
    public income: string,
    public expenditure: string,
    public liability: string,
    division: Division | null,
    costType: CostType | null,
    tags: Tag[],
    comments: Comment[]
  ) {
    this.costCenter1 = costCenter1;
    this.costCenter2 = costCenter2;
    this.costCenter3 = costCenter3;
    this.division = division;
    this.costType = costType;
    this.tags = tags;
    this.comments = comments;
  }
}
