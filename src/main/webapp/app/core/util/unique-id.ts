let id = 0;

export function uniqueId(): number {
  if (id >= Number.MAX_SAFE_INTEGER) {
    id = 0;
  } else {
    id += 1;
  }
  return id;
}
