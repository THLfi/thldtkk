package fi.thl.thldtkk.api.metadata.security;

public enum UserDirectory {
  /**
   * Application's internal user directory.
   */
  LOCAL,
  /**
   * THL kirjautumispalvelu
   */
  THL_SSO,
  VIRTU;

  public String getDirectoryUsername(String username) {
    return this.toString() + "/" + username;
  }
}
