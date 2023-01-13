package tests;

import models.homeWork.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.homeWork.CreateTestSpecs.createTestRequestSpec;
import static specs.homeWork.CreateTestSpecs.createTestResponseSpec;
import static specs.homeWork.DeletedTestSpecs.deletedTestRequestSpec;
import static specs.homeWork.DeletedTestSpecs.deletedTestResponseSpec;
import static specs.homeWork.LoginUnsuccessfulSpecs.loginUnsuccessfulRequestSpec;
import static specs.homeWork.LoginUnsuccessfulSpecs.loginUnsuccessfulResponseSpec;
import static specs.homeWork.RegisterUnsuccessfulSpecs.registerUnsuccessfulRequestSpec;
import static specs.homeWork.RegisterUnsuccessfulSpecs.registerUnsuccessfulResponseSpec;
import static specs.homeWork.UpdateTestSpec.updateTestRequestSpec;
import static specs.homeWork.UpdateTestSpec.updateTestResponseSpec;

public class HomeWork {

    @Test
    void createTest() {
        CreateTestBodyModel bodyModel = new CreateTestBodyModel();
        bodyModel.setName("morpheus");
        bodyModel.setJob("leader");

        CreateTestResponseModel responseModel = given(createTestRequestSpec)
                .body(bodyModel)
                .when()
                .post()
                .then()
                .spec(createTestResponseSpec)
                .extract().as(CreateTestResponseModel.class);

        assertThat(responseModel.getName()).isEqualTo("morpheus");
        assertThat(responseModel.getJob()).isEqualTo("leader");
    }

    @Test
    void updateTest() {
        UpdateTestBodyModel bodyModel = new UpdateTestBodyModel();
        bodyModel.setName("morpheus");
        bodyModel.setJob("zion resident");

        UpdateTestResponseModel responseModel = given(updateTestRequestSpec)
                .body(bodyModel)
                .when()
                .put()
                .then()
                .spec(updateTestResponseSpec)
                .extract().as(UpdateTestResponseModel.class);

        assertThat(responseModel.getJob()).isEqualTo("zion resident");
    }

    @Test
    void deletedTest() {
        given(deletedTestRequestSpec)
                .when()
                .delete()
                .then()
                .spec(deletedTestResponseSpec);
    }

    @Test
    void loginUnsuccessful() {
        LoginUnsuccessfulBodyModel bodyModel = new LoginUnsuccessfulBodyModel();
        bodyModel.setEmail("peter@klaven");

        LoginUnsuccessfulResponseModel responseModel = given(loginUnsuccessfulRequestSpec)
                .body(bodyModel)
                .when()
                .post()
                .then()
                .spec(loginUnsuccessfulResponseSpec)
                .extract().as(LoginUnsuccessfulResponseModel.class);

        assertThat(responseModel.getError()).isEqualTo("Missing password");
    }

    @Test
    void registerUnsuccessful() {
        RegisterUnsuccessfulBodyModel bodyModel = new RegisterUnsuccessfulBodyModel();
        bodyModel.setEmail("sydney@fife");

        RegisterUnsuccessfulResponseModel responseModel = given(registerUnsuccessfulRequestSpec)
                .body(bodyModel)
                .when()
                .post()
                .then()
                .spec(registerUnsuccessfulResponseSpec)
                .extract().as(RegisterUnsuccessfulResponseModel.class);

        assertThat(responseModel.getError()).isEqualTo("Missing password");
    }
}
