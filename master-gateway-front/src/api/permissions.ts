import { client } from './client'

export interface PermissionMetadata {
  permission: string
  dependencies: string[]
  allDependencies: string[]
}

export const permissionsApi = {
  getMetadata: (): Promise<PermissionMetadata[]> =>
    client.get('/permissions/metadata').then(r => r.data),
}
