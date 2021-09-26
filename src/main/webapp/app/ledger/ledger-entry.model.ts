import * as internal from 'node:stream';
import { Type } from 'class-transformer';
import { Comment } from 'app/shared/comment/comment.model';
import { normalizeText as tagNormalizeText } from 'app/entities/tag/tag-utils';
import { ID_UNDEFINED } from 'app/entities/constants';

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

export interface ITag {
  id: number;
  text: string;
  textNormalized: string;
}

export class Tag implements ITag {
  private _text!: string;

  private _textNormalized!: string;

  public id: number;

  constructor(id?: number, text?: string) {
    this.id = id ?? ID_UNDEFINED;
    this.text = text ?? '';
  }

  public static fromRawComponents(id: number, text: string, textNormalized: string): Tag {
    const instance = Object.create(this.prototype) as Tag;
    instance.id = id;
    instance._text = text;
    instance._textNormalized = textNormalized;
    return instance;
  }

  public static fromPojo(pojo: ITag): Tag {
    return this.fromRawComponents(pojo.id, pojo.text, pojo.textNormalized);
  }

  public toPojo(): ITag {
    return {
      id: this.id,
      text: this.text,
      textNormalized: this.textNormalized,
    };
  }

  public setTextRaw(value: string): void {
    this._text = value;
  }

  public setTextNormalizedRaw(value: string): void {
    this._textNormalized = value;
  }

  public get text(): string {
    return this._text;
  }

  public set text(value: string) {
    this._text = value;
    this._textNormalized = tagNormalizeText(value);
  }

  public get textNormalized(): string {
    return this._textNormalized;
  }

  public get displayText(): string {
    return this.text;
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
