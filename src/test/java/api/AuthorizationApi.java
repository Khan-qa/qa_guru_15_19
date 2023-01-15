package api;

import static io.restassured.RestAssured.given;

public class AuthorizationApi {

    public String ALLURE_TESTOPS_SESSION = "ALLURE_TESTOPS_SESSION";

    public String getXSRF_TOKEN() {
        return given()
                .get("/login")
                .then()
                .statusCode(200)
                .extract().response()
                .getCookie("XSRF-TOKEN");
    }

    public String getAuthorizationCookie(String username, String password) {
        String XSRF_TOKEN = getXSRF_TOKEN();

        return given()
                .header("Cookie", "XSRF-TOKEN=" + XSRF_TOKEN)
                .header("X-XSRF-TOKEN", XSRF_TOKEN)
                .formParam("username", username)
                .formParam("password", password)
                .post("/api/login/system")
                .then()
                .statusCode(200)
                .extract().response()
                .getCookie(ALLURE_TESTOPS_SESSION);
    }
}
