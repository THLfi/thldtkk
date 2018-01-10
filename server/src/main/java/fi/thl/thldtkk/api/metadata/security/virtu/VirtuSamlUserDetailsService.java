package fi.thl.thldtkk.api.metadata.security.virtu;

import com.google.common.collect.Sets;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.security.UserDirectory;
import fi.thl.thldtkk.api.metadata.security.UserRoles;
import fi.thl.thldtkk.api.metadata.security.UserWithProfile;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class VirtuSamlUserDetailsService implements SAMLUserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(VirtuSamlUserDetailsService.class);

  private final UserProfileService userProfileService;
  private final OrganizationService organizationService;

  public VirtuSamlUserDetailsService(UserProfileService userProfileService,
                                     OrganizationService organizationService) {
    this.userProfileService = userProfileService;
    this.organizationService = organizationService;
  }

  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    String virtuLocalID = getAttributeValue(credential, VirtuAttribute.virtuLocalID);
    String virtuHomeOrganization = getAttributeValue(credential, VirtuAttribute.virtuHomeOrganization);
    String virtuUsername = getVirtuUsername(virtuLocalID, virtuHomeOrganization);

    User userDelegate = new User(virtuUsername, "", true, true, true, true, getAuthorities(credential));

    String directoryUsername = UserDirectory.VIRTU.getDirectoryUsername(virtuUsername);

    Optional<UserProfile> existingUserProfile = userProfileService.getByExternalId(directoryUsername);
    UserProfile userProfile;
    if (existingUserProfile.isPresent()) {
      userProfile = existingUserProfile.get();

      LOG.debug("Found existing user profile for user '{}'", directoryUsername);
    }
    else {
      String firstName = getAttributeValue(credential, VirtuAttribute.givenName);
      String lastName = getAttributeValue(credential, VirtuAttribute.sn);
      String email = getAttributeValue(credential, VirtuAttribute.mail);

      Set<Organization> organizations = new HashSet<>();
      Optional<Organization> organization = organizationService.getByVirtuId(virtuHomeOrganization);
      if (organization.isPresent()) {
        organizations.add(organization.get());
      }
      else {
        String organizationName = getAttributeValue(credential, VirtuAttribute.o);
        Organization newOrganization = createNewOrganization(organizationName, virtuHomeOrganization);
        organizations.add(newOrganization);
      }

      userProfile = userProfileService.save(new UserProfile(
        UUID.randomUUID(),
        firstName,
        lastName,
        email,
        Sets.newHashSet(directoryUsername),
        Sets.newHashSet(UserRoles.USER),
        organizations));

      LOG.info("Created new user profile for user '{}'", directoryUsername);
    }

    return new UserWithProfile(
      directoryUsername,
      userDelegate,
      userProfile);
  }

  private String getVirtuUsername(String virtuLocalUser, String virtuHomeOrganization) {
    return new StringBuilder()
      .append(virtuLocalUser)
      .append('@')
      .append(virtuHomeOrganization)
      .toString();
  }

  private String getAttributeValue(SAMLCredential credential, VirtuAttribute attribute) {
    String value = credential.getAttributeAsString(attribute.oid);
    if (attribute.required) {
      Assert.hasText(value, String.format("Required attribute '%s' is empty", attribute));
    }
    return value;
  }

  private Collection<GrantedAuthority> getAuthorities(SAMLCredential credential) {
    String[] entitlements = getAttributeValues(credential, VirtuAttribute.virtuPersonEntitlement);
    Collection<GrantedAuthority> authorities;
    if (entitlements != null && entitlements.length > 0) {
      authorities = Arrays.stream(entitlements)
        .map(entitlement -> new SimpleGrantedAuthority(entitlement))
        .collect(Collectors.toSet());
    }
    else {
      authorities = Collections.emptySet();
    }
    return authorities;
  }

  private String[] getAttributeValues(SAMLCredential credential, VirtuAttribute attribute) {
    String[] values = credential.getAttributeAsStringArray(attribute.oid);
    if (attribute.required) {
      Assert.notEmpty(values, String.format("Required attribute '%s' does not have any values", attribute));
    }
    return values;
  }

  private Organization createNewOrganization(String name, String virtuId) {
    if (!StringUtils.hasText(name)) {
      name = virtuId;
    }

    Map<String, String> prefLabel = new LinkedHashMap<>();
    prefLabel.put("fi",  name);

    Organization newOrganization = new Organization(
      UUID.randomUUID(),
      prefLabel,
      new LinkedHashMap<>(),
      new LinkedHashMap<>(),
      "",
      asList(virtuId)
    );

    Organization savedOrganization = organizationService.save(newOrganization);

    LOG.info("Created new organization '{}' ('{}')", name, virtuId);

    return savedOrganization;
  }

}
