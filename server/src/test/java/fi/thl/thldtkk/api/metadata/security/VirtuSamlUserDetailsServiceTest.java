package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.security.virtu.VirtuAttribute;
import fi.thl.thldtkk.api.metadata.security.virtu.VirtuSamlUserDetailsService;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import fi.thl.thldtkk.api.metadata.test.an;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class VirtuSamlUserDetailsServiceTest {

  SAMLUserDetailsService service;

  @Mock
  UserProfileService userProfileService;
  @Mock
  OrganizationService organizationService;
  @Mock
  SAMLCredential credential;

  @Before
  public void initServiceAndMocks() {
    MockitoAnnotations.initMocks(this);
    service = new VirtuSamlUserDetailsService(userProfileService, organizationService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyUsername() {
    mockCredentials("     ", "thl.fi");

    service.loadUserBySAML(credential);
  }

  private void mockCredentials(String virtuLocalId, String virtuHomeOrganization) {
    when(credential.getAttributeAsString(eq(VirtuAttribute.virtuLocalID.oid)))
      .thenReturn(virtuLocalId);
    when(credential.getAttributeAsString(eq(VirtuAttribute.virtuHomeOrganization.oid)))
      .thenReturn(virtuHomeOrganization);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyHomeOrganization() {
    mockCredentials("usr", "    ");

    service.loadUserBySAML(credential);
  }

  @Test
  public void existingUserProfileFoundWithIdenticalInfo() {
    mockCredentials("usr", "thl.fi");

    UserProfile existingProfile = new UserProfile(
      nameUUIDFromString("usr"),
      "First",
      "Last",
      "first.last@example.com",
      new HashSet(Arrays.asList("VIRTU/usr@thl.fi")),
      new HashSet(Arrays.asList(UserRoles.USER)),
      Collections.emptySet());
    when(userProfileService.getByExternalId(eq("VIRTU/usr@thl.fi")))
      .thenReturn(Optional.of(existingProfile));

    service.loadUserBySAML(credential);
  }

  @Test
  public void createNewUserProfileWithExistingOrganization() {
    mockCredentials("usr", "thl.fi");

    when(credential.getAttributeAsString(eq(VirtuAttribute.givenName.oid))).thenReturn("First");
    when(credential.getAttributeAsString(eq(VirtuAttribute.sn.oid))).thenReturn("Last");
    when(credential.getAttributeAsString(eq(VirtuAttribute.mail.oid))).thenReturn("first.last@thl.fi");

    when(userProfileService.getByExternalId(eq("VIRTU/usr@thl.fi")))
      .thenReturn(Optional.empty());

    Organization organization = an.organization().withVirtuId("thl.fi").build();
    when(organizationService.getByVirtuId(eq("thl.fi"))).thenReturn(Optional.of(organization));

    ArgumentCaptor<UserProfile> newUserProfileArgumentCaptor = ArgumentCaptor.forClass(UserProfile.class);
    when(userProfileService.save(newUserProfileArgumentCaptor.capture()))
      .thenReturn(new UserProfile(UUID.randomUUID()));

    service.loadUserBySAML(credential);

    UserProfile savedUserProfile = newUserProfileArgumentCaptor.getValue();
    assertThat(savedUserProfile.getFirstName().get()).isEqualTo("First");
    assertThat(savedUserProfile.getLastName().get()).isEqualTo("Last");
    assertThat(savedUserProfile.getEmail().get()).isEqualTo("first.last@thl.fi");
    assertThat(savedUserProfile.getExternalIds()).containsExactly("VIRTU/usr@thl.fi");
    assertThat(savedUserProfile.getRoles()).containsExactly(UserRoles.USER);
    assertThat(savedUserProfile.getOrganizations()).containsExactly(organization);
  }

  @Test
  public void createNewOrganizationWithName() {
    mockCredentials("usr", "neworg.fi");

    when(credential.getAttributeAsString(eq(VirtuAttribute.givenName.oid))).thenReturn("First");
    when(credential.getAttributeAsString(eq(VirtuAttribute.sn.oid))).thenReturn("Last");
    when(credential.getAttributeAsString(eq(VirtuAttribute.mail.oid))).thenReturn("first.last@neworg.fi");
    when(credential.getAttributeAsString(eq(VirtuAttribute.o.oid))).thenReturn("New Org");

    when(userProfileService.getByExternalId(eq("VIRTU/usr@neworg.fi")))
      .thenReturn(Optional.empty());

    when(organizationService.getByVirtuId(eq("neworg.fi"))).thenReturn(Optional.empty());

    ArgumentCaptor<Organization> newOrganizationArgumentCaptor = ArgumentCaptor.forClass(Organization.class);
    Organization organizationAfterSave = an.organization().build();
    when(organizationService.save(newOrganizationArgumentCaptor.capture())).thenReturn(organizationAfterSave);

    ArgumentCaptor<UserProfile> newUserProfileArgumentCaptor = ArgumentCaptor.forClass(UserProfile.class);
    when(userProfileService.save(newUserProfileArgumentCaptor.capture()))
      .thenReturn(new UserProfile(UUID.randomUUID()));

    service.loadUserBySAML(credential);

    Organization savedOrganization = newOrganizationArgumentCaptor.getValue();
    assertThat(savedOrganization.getId()).isNotNull();
    assertThat(savedOrganization.getPrefLabel().get("fi")).isEqualTo("New Org");
    assertThat(savedOrganization.getAbbreviation()).isNotNull().isEmpty();
    assertThat(savedOrganization.getVirtuIds()).containsExactly("neworg.fi");

    UserProfile savedUserProfile = newUserProfileArgumentCaptor.getValue();
    assertThat(savedUserProfile.getOrganizations()).containsExactly(organizationAfterSave);
  }

  @Test
  public void createNewOrganizationWithoutName() {
    mockCredentials("usr", "neworg.fi");

    when(credential.getAttributeAsString(eq(VirtuAttribute.givenName.oid))).thenReturn("First");
    when(credential.getAttributeAsString(eq(VirtuAttribute.sn.oid))).thenReturn("Last");
    when(credential.getAttributeAsString(eq(VirtuAttribute.mail.oid))).thenReturn("first.last@neworg.fi");
    when(credential.getAttributeAsString(eq(VirtuAttribute.o.oid))).thenReturn("   ");

    when(userProfileService.getByExternalId(eq("VIRTU/usr@neworg.fi")))
      .thenReturn(Optional.empty());

    when(organizationService.getByVirtuId(eq("neworg.fi"))).thenReturn(Optional.empty());

    ArgumentCaptor<Organization> newOrganizationArgumentCaptor = ArgumentCaptor.forClass(Organization.class);
    Organization organizationAfterSave = an.organization().build();
    when(organizationService.save(newOrganizationArgumentCaptor.capture())).thenReturn(organizationAfterSave);

    ArgumentCaptor<UserProfile> newUserProfileArgumentCaptor = ArgumentCaptor.forClass(UserProfile.class);
    when(userProfileService.save(newUserProfileArgumentCaptor.capture()))
      .thenReturn(new UserProfile(UUID.randomUUID()));

    service.loadUserBySAML(credential);

    Organization savedOrganization = newOrganizationArgumentCaptor.getValue();
    assertThat(savedOrganization.getId()).isNotNull();
    assertThat(savedOrganization.getPrefLabel().get("fi")).isEqualTo("neworg.fi");
    assertThat(savedOrganization.getVirtuIds()).containsExactly("neworg.fi");

    UserProfile savedUserProfile = newUserProfileArgumentCaptor.getValue();
    assertThat(savedUserProfile.getOrganizations()).containsExactly(organizationAfterSave);
  }

}
