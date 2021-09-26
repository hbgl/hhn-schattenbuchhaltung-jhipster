export function tick(): Promise<void> {
  return new Promise(r => setTimeout(() => r()));
}
