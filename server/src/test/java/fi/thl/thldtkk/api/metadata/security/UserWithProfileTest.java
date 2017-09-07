package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class UserWithProfileTest {

  @Mock
  UserDetails delegate;
  @Mock
  UserProfile userProfile;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void providedUsernameShouldBeUsed() {
    UserWithProfile user = new UserWithProfile("LOCAL/jeff", delegate, userProfile);
    assertThat(user.getUsername()).isEqualTo("LOCAL/jeff");
  }

  @Test
  public void authoritiesShouldBeCombinedFromDelegateUserAndProfile() {
    // Mock wired this way because of getAuthorities() return type
    doReturn(Arrays.asList(
      (GrantedAuthority) (() -> "user"),
      (GrantedAuthority) (() -> "boss")
    )).when(delegate).getAuthorities();

    when(userProfile.getRoles()).thenReturn(new LinkedHashSet<>(Arrays.asList("user", "admin")));

    UserWithProfile user = new UserWithProfile("LOCAL/jeff", delegate, userProfile);

    assertContainsAuthorities(user.getAuthorities(), "user", "boss", "admin");
  }

  private void assertContainsAuthorities(Collection<? extends GrantedAuthority> actual, String... expected) {
    assertThat(actual)
      .describedAs("Total number of authorities")
      .hasSize(expected.length);
    Iterator<? extends GrantedAuthority> iterator = actual.iterator();
    for (int i = 0; i < expected.length; i++) {
      assertThat(iterator.next().getAuthority())
        .describedAs("Authority role #%1$d", i)
        .isEqualTo(expected[i]);
    }
  }

}
