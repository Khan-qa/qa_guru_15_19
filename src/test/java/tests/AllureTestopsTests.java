package tests;

import api.AuthorizationApi;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import models.lombok.CreateTestCaseWithApiTestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.is;

public class AllureTestopsTests {

    AuthorizationApi authorizationApi = new AuthorizationApi();
    CreateTestCaseWithApiTestModel bodyModel = new CreateTestCaseWithApiTestModel();
    Faker faker = new Faker();

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
    void loginTest() {
        open("/");

        $(byName("username")).setValue(USERNAME);
        $(byName("password")).setValue(PASSWORD).pressEnter();

        $("button[aria-label='User menu']").click();
        $("div.Menu__item_info b").shouldHave(text(USERNAME));
    }

    @Test
    void loginWithApiTest() {
        String XSRF_TOKEN = given()
                .get("/login")
                .then()
                .statusCode(200)
                .extract().response()
                .getCookie("XSRF-TOKEN");

        String auth = given()
                .header("Cookie", "XSRF-TOKEN=" + XSRF_TOKEN)
                .header("X-XSRF-TOKEN", XSRF_TOKEN)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .post("/api/login/system")
                .then()
                .statusCode(200)
                .extract().response()
                .getCookie(ALLURE_TESTOPS_SESSION);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, auth));

        open("/");
        $("button[aria-label='User menu']").click();
        $("div.Menu__item_info b").shouldHave(text(USERNAME));
    }

    @Test
    void loginWithSimpleApiTest() {
        AuthorizationApi authorizationApi = new AuthorizationApi();
        String authorizationCookie = authorizationApi.getAuthorizationCookie(USERNAME, PASSWORD);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/");
        $("button[aria-label='User menu']").click();
        $("div.Menu__item_info b").shouldHave(text(USERNAME));
    }

    @Test
    void viewTestCaseTest() {
        AuthorizationApi authorizationApi = new AuthorizationApi();
        String authorizationCookie = authorizationApi.getAuthorizationCookie(USERNAME, PASSWORD);

        given().log().all()
                .cookie(ALLURE_TESTOPS_SESSION, authorizationCookie)
                .get("/api/rs/testcase/14306/overview")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("view test case name"));
    }

    @Test
    void viewTestCaseWithUiTest() {
        AuthorizationApi authorizationApi = new AuthorizationApi();
        String authorizationCookie = authorizationApi.getAuthorizationCookie(USERNAME, PASSWORD);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/project/1807/test-cases/14306");
        $(".TestCaseLayout__name").shouldHave(text("view test case name"));
    }

    @Test
    void createTestCaseWithApiTest() {
        String fakeName = faker.name().nameWithMiddle();
        String authorizationCookie = authorizationApi.getAuthorizationCookie(USERNAME, PASSWORD);
        String xsrfToken = authorizationApi.getXSRF_TOKEN();

        bodyModel.setName(fakeName);

        int testCaseId = given()
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

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/project/1807/test-cases/" + testCaseId);
        $(".TestCaseLayout__name").shouldHave(text(fakeName));
    }
}
