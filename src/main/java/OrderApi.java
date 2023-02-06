import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderApi extends SpecificationAPI {

    public static final String PATH_API = "/api/orders";

    public ValidatableResponse createOrderWithoutAuth(Ingredient ingredient) {
        return given()
                // .log().all()
                .spec(getSpec())
                .body(ingredient)
                .when()
                .post(PATH_API)
                .then();
        // .log().all();
    }

    public ValidatableResponse createOrderWithAuth(String accessToken, Ingredient ingredient) {
        return given()
                // .log().all()
                .spec(getSpec())
                .auth()
                .oauth2(accessToken)
                .body(ingredient)
                .when()
                .post(PATH_API)
                .then();
        // .log().all();
    }

    public ValidatableResponse getOrderCurrentUserWithAuth(String accessToken) {
        return given()
                // .log().all()
                .spec(getSpec())
                .auth()
                .oauth2(accessToken)
                .when()
                .get(PATH_API)
                .then();
        // .log().all();
    }

    public ValidatableResponse getOrderCurrentUserWithoutAuth() {
        return given()
                // .log().all()
                .spec(getSpec())
                .when()
                .get(PATH_API)
                .then();
        // .log().all();
    }


}
