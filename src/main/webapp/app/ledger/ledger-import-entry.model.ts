export class CostCenterImport {
  constructor(public no: string, public name: string) {}

  public get displayText(): string {
    return `${this.no} (${this.name})`;
  }
}

export class LedgerImportEntry {
  constructor(
    public year: string,
    public costCenter1: CostCenterImport,
    public costCenter2: CostCenterImport,
    public costCenter3: CostCenterImport,
    public no: string,
    public description: string,
    public aNo: string | null,
    public bookingDate: string,
    public income: string,
    public expenditure: string,
    public liability: string,
    public divisionNo: string | null,
    public divisionName: string | null,
    public costTypeNo: string | null,
    public costTypeName: string | null
  ) {}
}
