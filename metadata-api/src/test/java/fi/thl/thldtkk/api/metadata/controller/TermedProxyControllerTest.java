package fi.thl.thldtkk.api.metadata.controller;

import com.jayway.restassured.http.ContentType;
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

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TermedProxyControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private TermedProxyController controller;

    @Before
    public void initializeWebAppContext() {
        RestAssuredMockMvc.webAppContextSetup(context);
    }

    @After
    public void resetWebAppContext() {
        RestAssuredMockMvc.reset();
    }

    @Test
    public void testGetAllDataSets() {
        given()
            .standaloneSetup(controller)
        .when()
            .get("/termed/datasets?max=-1")
        .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .contentType(ContentType.JSON)
            .and()
            .body(containsString("fi"));
    }

    @Test
    public void testGetOrganizations() {
        given()
            .standaloneSetup(controller)
        .when()
            .get("/termed/types/Organization/nodes")
        .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .contentType(ContentType.JSON)
            .and()
            .body(containsString("THL"));
    }
}
