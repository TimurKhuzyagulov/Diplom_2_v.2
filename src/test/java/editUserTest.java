import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class editUserTest {

    static UserApi userApi;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @Test
    @DisplayName("Редактирование пользователя с авторизацией")
    @Description("Проверка редактирование пользователя с авторизацией")
    public void editUserWithAuthTest() {
        userApi.create(ListUsers.userTestForEdit);
        ValidatableResponse responseAuth = userApi.authorization(ListUsers.userTestForEdit);
        String accessTokenWithPrefix = responseAuth.extract().path("accessToken");
        String accessToken = accessTokenWithPrefix.substring(7);

        ValidatableResponse responseUpdate = userApi.update(accessToken, ListUsers.userDataUpd);

        int statusCode = responseUpdate.extract().statusCode();
        boolean successActual = responseUpdate.extract().path("success");

        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertTrue(successActual);

        Response response = given().auth().oauth2(accessToken).get("https://stellarburgers.nomoreparties.site/api/auth/user");

        UserFullPOJO userFullPOJO = response.body().as(UserFullPOJO.class);
        Assert.assertEquals(ListUsers.userDataUpd.getEmail(), userFullPOJO.getUser().getEmail());
        Assert.assertEquals(ListUsers.userDataUpd.getName(), userFullPOJO.getUser().getName());

        userApi.delete(accessToken);
    }


    @Test
    @DisplayName("Редактирование пользователя без авторизацией")
    @Description("Проверка редактирование пользователя без авторизацией")
    public void editUserWithoutAuthTest() {
        userApi.create(ListUsers.userTestForEdit);

        ValidatableResponse responseUpdate = userApi.update("", ListUsers.userDataUpd);

        int statusCode = responseUpdate.extract().statusCode();
        boolean successActual = responseUpdate.extract().path("success");

        Assert.assertEquals(SC_UNAUTHORIZED, statusCode);
        Assert.assertFalse(successActual);
    }

    @Test
    @DisplayName("Редактирование пользователя с существующим email")
    @Description("Проверка редактирование пользователя с указанием существующего email")
    public void editUserWithEmailExists() {
        userApi.create(ListUsers.userDataUpdWithExistsEmail);
        ValidatableResponse responseAuth = userApi.authorization(ListUsers.userDataUpdWithExistsEmail);
        String accessTokenWithPrefix = responseAuth.extract().path("accessToken");
        String accessToken = accessTokenWithPrefix.substring(7);

        ValidatableResponse responseUpdateWithEmailExists = userApi.update(accessToken, ListUsers.existsEmail);

        int statusCode = responseUpdateWithEmailExists.extract().statusCode();
        boolean successActual = responseUpdateWithEmailExists.extract().path("success");
        String messageActual = responseUpdateWithEmailExists.extract().path("message");

        Assert.assertEquals(SC_FORBIDDEN, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("User with such email already exists", messageActual);
        userApi.delete(accessToken);
    }

}
