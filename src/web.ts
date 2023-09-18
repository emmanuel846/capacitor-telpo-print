import { WebPlugin } from '@capacitor/core';

import type { TelpoPrintPlugin } from './definitions';

export class TelpoPrintWeb extends WebPlugin implements TelpoPrintPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
