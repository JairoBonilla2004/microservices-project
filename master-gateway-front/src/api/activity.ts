import { client } from './client'

export interface ActivityEntry {
  entityType: string
  entityName: string
  action: string
  actor: string
  timestamp: string
}

export const activityApi = {
  /** GET /api/activity/recent?limit={limit} */
  getRecent: (limit = 10) =>
    client.get<ActivityEntry[]>('/activity/recent', { params: { limit } }).then(r => r.data),
}
