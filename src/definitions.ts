export interface TelpoPrintPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
