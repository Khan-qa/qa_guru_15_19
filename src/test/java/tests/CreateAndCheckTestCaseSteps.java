package tests;

import api.AuthorizationApi;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import models.homeWork.CreateTestCaseStepsModel;
import models.homeWork.Step;
import models.lombok.CreateTestCaseWithApiTestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.is;

public class CreateAndCheckTestCaseSteps {


    AuthorizationApi authorizationApi = new AuthorizationApi();
    Faker faker = new Faker();

    String fakeName = faker.name().nameWithMiddle();
    String authorizationCookie = authorizationApi.getAuthorizationCookie(USERNAME, PASSWORD);
    String xsrfToken = authorizationApi.getXSRF_TOKEN();

    private final static String
            USERNAME = "allure8",
            PASSWORD = "allure8",
            ALLURE_TESTOPS_SESSION = "ALLURE_TESTOPS_SESSION",
            XSRF_TOKEN = "XSRF-TOKEN";

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://allure.autotests.cloud";
        RestAssured.baseURI = "https://allure.autotests.cloud";
    }

    @Test
    void createTestCaseSteps() {
        int testCaseId = getIdNewTestCase();
        CreateTestCaseStepsModel bodyModel = new CreateTestCaseStepsModel();
        List<Step> stepList = new ArrayList<>();
        int listSize = faker.number().numberBetween(1, 10);

        for (int i = 0; i < listSize; i++) {
            Step step = new Step();
            step.setName(faker.name().nameWithMiddle());
            stepList.add(step);
        }
        bodyModel.setSteps(stepList);

        given()
                .cookies(XSRF_TOKEN, xsrfToken,
                        ALLURE_TESTOPS_SESSION, authorizationCookie)
                .header("X-XSRF-TOKEN", xsrfToken)
                .contentType(JSON)
                .body(bodyModel)
                .post("/api/rs/testcase/" + testCaseId + "/scenario")
                .then()
                .statusCode(200);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/project/1807/test-cases/" + testCaseId);
        for (int i = 0; i < stepList.size(); i++) {
            $$(".TreeElement__node").get(i).shouldHave(text(stepList.get(i).getName()));
        }
    }


    Integer getIdNewTestCase() {
        CreateTestCaseWithApiTestModel bodyModel = new CreateTestCaseWithApiTestModel();

        bodyModel.setName(fakeName);

        return RestAssured.given()
                .log().all()
                .cookies(XSRF_TOKEN, xsrfToken,
                        ALLURE_TESTOPS_SESSION, authorizationCookie)
                .header("X-XSRF-TOKEN", xsrfToken)
                .contentType(JSON)
                .body(bodyModel)
                .queryParam("projectId", "1807")
                .post("/api/rs/testcasetree/leaf")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is(fakeName))
                .body("automated", is(false))
                .body("external", is(false))
                .extract()
                .path("id");
    }
}
