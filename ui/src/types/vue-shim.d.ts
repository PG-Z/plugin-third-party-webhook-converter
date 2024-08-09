import { FormKitContext } from '@formkit/core';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $formkit: FormKitContext;
  }
}
