import * as internal from 'node:stream';
import { Type } from 'class-transformer';

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

export class LedgerEntry {
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
