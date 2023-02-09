import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserApi extends SpecificationAPI {


    private static final String PATH_CREATE = "/api/auth/register";
    private static final String PATH_AUTH = "/api/auth/login";
    private static final String PATH_DELETE_UPDATE = "/api/auth/user";

    public ValidatableResponse create(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    public ValidatableResponse authorization(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH_AUTH)
                .then();
    }

    public ValidatableResponse delete(String accessToken) {
        return given()
                // .log().all()
                .spec(getSpec())
                .auth()
                .oauth2(accessToken)
                .when()
                .delete(PATH_DELETE_UPDATE)
                .then();
        // .log().all();

    }

    public ValidatableResponse update(String accessToken, User user) {
        return given()
                // .log().all()
                .spec(getSpec())
                .auth()
                .oauth2(accessToken)
                .body(user)
                .when()
                .patch(PATH_DELETE_UPDATE)
                .then();
        // .log().all();

    }
}
