export interface User {
  username: string
  firstName?: string
  lastName?: string
  email?: string
  isLoggedIn: boolean
  isAdmin: boolean
  isOrganizationAdmin: boolean
}
