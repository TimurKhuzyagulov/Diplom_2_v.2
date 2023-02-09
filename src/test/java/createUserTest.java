import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class createUserTest {

    static UserApi userApi;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void afterTest() {
        ValidatableResponse responseAuth = userApi.authorization(ListUsers.userTestDefault);
        String accessTokenWithPrefix = responseAuth.extract().path("accessToken");
        String accessToken = accessTokenWithPrefix.substring(7);
        userApi.delete(accessToken);
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создание уникального пользователя")
    public void createUniqueUserTest() {
        ValidatableResponse responseCreate = userApi.create(ListUsers.userTestDefault);

        int statusCode = responseCreate.extract().statusCode();
        boolean successActual = responseCreate.extract().path("success");

        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertTrue(successActual);
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Проверка создание существующего пользователя")
    public void createDoubleUserTest() {
        userApi.create(ListUsers.userTestDefault);
        ValidatableResponse responseCreateDouble = userApi.create(ListUsers.userTestDefault);

        int statusCode = responseCreateDouble.extract().statusCode();
        boolean successActual = responseCreateDouble.extract().path("success");

        Assert.assertEquals(SC_FORBIDDEN, statusCode);
        Assert.assertFalse(successActual);
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка создание пользователя без указания email")
    public void createUserWithoutEmailTest() {
        userApi.create(ListUsers.userTestDefault);
        ValidatableResponse responseCreateWithoutEmail = userApi.create(ListUsers.userTestWithoutEmail);

        int statusCode = responseCreateWithoutEmail.extract().statusCode();
        boolean successActual = responseCreateWithoutEmail.extract().path("success");
        String messageActual = responseCreateWithoutEmail.extract().path("message");

        Assert.assertEquals(SC_FORBIDDEN, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("Email, password and name are required fields", messageActual);
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка создание пользователя без указания пароля")
    public void createUserWithoutPasswordTest() {
        userApi.create(ListUsers.userTestDefault);
        ValidatableResponse responseCreateWithoutPassword = userApi.create(ListUsers.userTestWithoutPassword);

        int statusCode = responseCreateWithoutPassword.extract().statusCode();
        boolean successActual = responseCreateWithoutPassword.extract().path("success");
        String messageActual = responseCreateWithoutPassword.extract().path("message");

        Assert.assertEquals(SC_FORBIDDEN, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("Email, password and name are required fields", messageActual);
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка создание пользователя без указания имени")
    public void createUserWithoutNameTest() {
        userApi.create(ListUsers.userTestDefault);
        ValidatableResponse responseCreateWithoutName = userApi.create(ListUsers.userTestWithoutName);

        int statusCode = responseCreateWithoutName.extract().statusCode();
        boolean successActual = responseCreateWithoutName.extract().path("success");
        String messageActual = responseCreateWithoutName.extract().path("message");

        Assert.assertEquals(SC_FORBIDDEN, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("Email, password and name are required fields", messageActual);
    }
}
