import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class createOrderTest {

    static OrderApi orderApi;
    static UserApi userApi;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        userApi = new UserApi();
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации пользователя")
    public void createOrderWithoutAuthTest() {


        ValidatableResponse responseCreateOrderWithoutAuth = orderApi.createOrderWithoutAuth(ListIngredients.ingredientsDefoult);

        int statusCode = responseCreateOrderWithoutAuth.extract().statusCode();
        boolean successActual = responseCreateOrderWithoutAuth.extract().path("success");

        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertTrue(successActual);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией пользователя")
    public void createOrderWitAuthTest() {
        userApi.create(ListUsers.userTestDefault);
        ValidatableResponse responseAuth = userApi.authorization(ListUsers.userTestDefault);
        String accessTokenWithPrefix = responseAuth.extract().path("accessToken");
        String accessToken = accessTokenWithPrefix.substring(7);

        ValidatableResponse responseCreateOrderWithAuth = orderApi.createOrderWithAuth(accessToken, ListIngredients.ingredientsDefoult);
        int statusCode = responseCreateOrderWithAuth.extract().statusCode();
        boolean successActual = responseCreateOrderWithAuth.extract().path("success");

        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertTrue(successActual);

        userApi.delete(accessToken);
    }

    @Test
    @DisplayName("Создание заказа без ингредиента")
    @Description("Проверка создания заказа без ингридиентов")
    public void createOrderWithoutIngredients() {
        ValidatableResponse responseCreateOrderWithoutIngredients = orderApi.createOrderWithoutAuth(ListIngredients.ingredientsEmpty);

        int statusCode = responseCreateOrderWithoutIngredients.extract().statusCode();
        boolean successActual = responseCreateOrderWithoutIngredients.extract().path("success");
        String messageActual = responseCreateOrderWithoutIngredients.extract().path("message");

        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("Ingredient ids must be provided", messageActual);
    }

    @Test
    @DisplayName("Создание заказа с неверным хэшом")
    @Description("Проверка создания заказа с использованием неверного хэща ингредиента")
    public void createOrderWithIncorrectHashIngredients() {
        ValidatableResponse responseWithIncorrectHashIngredients = orderApi.createOrderWithoutAuth(ListIngredients.ingredientsIncorrect);

        int statusCode = responseWithIncorrectHashIngredients.extract().statusCode();

        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}
