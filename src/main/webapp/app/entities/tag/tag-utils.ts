export const TAG_SEPARATOR = ',';

export function removeSeparator(input: string): string {
  return input.replace(TAG_SEPARATOR, '');
}

export function normalizeText(input: string): string {
  return input.trim().toLowerCase().replace(TAG_SEPARATOR, '');
}
