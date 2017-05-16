package fi.thl.thldtkk.api.metadata.controller;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasetControllerTest {

  @Autowired
  private WebApplicationContext context;
  @Autowired
  private DatasetController controller;

  @Before
  public void initializeWebAppContext() {
    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig(ObjectMapperType.GSON));
    RestAssuredMockMvc.webAppContextSetup(context);
  }

  @After
  public void resetWebAppContext() {
    RestAssuredMockMvc.reset();
  }

  @Test
  public void shouldQueryAllDatasets() {
    given()
        .standaloneSetup(controller)
        .when()
        .get("/api/datasets?max=-1")
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(containsString("fi"));
  }

}
