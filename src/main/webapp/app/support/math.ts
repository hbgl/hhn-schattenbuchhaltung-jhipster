export function addUndef(n: number | undefined, addend: number): number | undefined {
  if (n === undefined) {
    return n;
  }
  return n + addend;
}

export function subUndef(n: number | undefined, addend: number): number | undefined {
  return addUndef(n, -addend);
}
