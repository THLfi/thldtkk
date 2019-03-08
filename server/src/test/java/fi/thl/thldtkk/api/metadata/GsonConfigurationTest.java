package fi.thl.thldtkk.api.metadata;

import com.google.gson.Gson;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.test.a;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonConfigurationTest {

  Gson gson;

  @Before
  public void initGsonBean() {
    gson = new GsonConfiguration().gson();
  }

  /**
   * GSON required that classes have default no-parameters constructor or
   * otherwise class member default values won't be initialized correctly.
   */
  @Test
  public void missingCollectionFieldsShouldKeepTheirDefaultValue() {
    assertThat(gson.fromJson("{}", Study.class).getLinks())
      .isNotNull();
    assertThat(gson.fromJson("{}", Dataset.class).getLinks())
      .isNotNull();
    assertThat(gson.fromJson("{}", InstanceVariable.class).getConceptsFromScheme())
      .isNotNull();
    assertThat(gson.fromJson("{}", Organization.class).getOrganizationUnits())
      .isNotNull();
    assertThat(gson.fromJson("{}", CodeList.class).getCodeItems())
      .isNotNull();
  }

  @Test
  public void dateFormatShouldBeInIsoFormat() {
    java.util.Date lastModified = Date.from(ZonedDateTime.of(2019, 3, 8, 14, 43, 11, 0, ZoneId.of("Europe/Helsinki")).toInstant());
    String json = gson.toJson(a.study().withLastModifiedDate(lastModified));
    assertThat(json).contains("\"lastModifiedDate\": \"2019-03-08T14:43:11.000+02:00\"");
  }

}
