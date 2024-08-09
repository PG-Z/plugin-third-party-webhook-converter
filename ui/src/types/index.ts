export interface Metadata {
  name: string;
  generateName?: string;
  labels?: {
    [key: string]: string;
  } | null;
  annotations?: {
    [key: string]: string;
  } | null;
  version?: number | null;
  creationTimestamp?: string | null;
  deletionTimestamp?: string | null;
}

export interface WebhookGroupSpec {
  displayName: string;
  priority?: number;
}

export interface WebhookSpec {
  displayName: string;
  webhookType: string;
  convertType: string;
  webhookSign: string;
  description?: string;
  priority?: number;
  groupName?: string;
}

export interface Webhook {
  spec: WebhookSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface WebhookGroup {
  spec: WebhookGroupSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface WebhookList {
  page: number;
  size: number;
  total: number;
  items: Array<Webhook>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

export interface WebhookGroupList {
  page: number;
  size: number;
  total: number;
  items: Array<WebhookGroup>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}
