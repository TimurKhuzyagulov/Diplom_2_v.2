import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class loginUserTest {

    static UserApi userApi;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверка авторизации существующего пользователя")
    public void loginRealUserTest() {
        userApi.create(ListUsers.userTestDefault);
        ValidatableResponse responseAuth = userApi.authorization(ListUsers.userTestDefault);
        String accessTokenWithPrefix = responseAuth.extract().path("accessToken");
        String accessToken = accessTokenWithPrefix.substring(7);

        int statusCode = responseAuth.extract().statusCode();
        boolean successActual = responseAuth.extract().path("success");

        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertTrue(successActual);
        Assert.assertNotNull(responseAuth.extract().path("accessToken"));
        Assert.assertNotNull(responseAuth.extract().path("refreshToken"));

        userApi.delete(accessToken);
    }

    @Test
    @DisplayName("Авторизация не существующего пользователя")
    @Description("Проверка авторизации не существующего пользователя")
    public void loginNotRealUserTest() {
        ValidatableResponse responseAuth = userApi.authorization(ListUsers.userTestNotReal);

        int statusCode = responseAuth.extract().statusCode();
        boolean successActual = responseAuth.extract().path("success");
        String messageActual = responseAuth.extract().path("message");

        Assert.assertEquals(SC_UNAUTHORIZED, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("email or password are incorrect", messageActual);
    }
}
