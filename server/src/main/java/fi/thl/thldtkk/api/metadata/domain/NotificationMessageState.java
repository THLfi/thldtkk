package fi.thl.thldtkk.api.metadata.domain;

// Notification message (ie. email) state that provides more flexibility over a boolean
// field and potentially reduces the need for migrations in the future
public enum NotificationMessageState {
  PENDING,
  SENT
}
