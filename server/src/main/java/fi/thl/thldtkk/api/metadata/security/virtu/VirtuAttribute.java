package fi.thl.thldtkk.api.metadata.security.virtu;

/**
 * See https://wiki.eduuni.fi/display/CSCVIRTU/Virtun+attribuutit for more information.
 */
public enum VirtuAttribute {

  /**
   * User's ID (e.g. username in home organization).
   */
  virtuLocalID("urn:oid:1.3.6.1.4.1.31350.1.8", true),
  /**
   * User's organization ID.
   */
  virtuHomeOrganization("urn:oid:1.3.6.1.4.1.31350.1.5", true),
  /**
   * User's full name.
   */
  cn("urn:oid:2.5.4.3", true),
  /**
   * User's first name.
   */
  givenName("urn:oid:2.5.4.42", true),
  /**
   * User's last name.
   */
  sn("urn:oid:2.5.4.4", true),
  /**
   * User's email address.
   */
  mail("urn:oid:0.9.2342.19200300.100.1.3", false),
  /**
   * User's authorities.
   */
  virtuPersonEntitlement("urn:oid:1.3.6.1.4.1.31350.1.4", false),
  /**
   * User's organization name.
   */
  o("urn:oid:2.5.4.10", false);

  public final String oid;
  public final boolean required;

  VirtuAttribute(String oid, boolean required) {
    this.oid = oid;
    this.required = required;
  }

}
