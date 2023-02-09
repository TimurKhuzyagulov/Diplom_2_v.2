import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
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

        //десериализуем ответ метода по получение ингредиентов и на его основе формируем тело для запроса по созданию заказа
        Response responseGetIngredient = given().get("https://stellarburgers.nomoreparties.site/api/ingredients");
        IngredientFullPOJO ingredientFullPOJO = responseGetIngredient.body().as(IngredientFullPOJO.class);
        String[] ingredientsString = {ingredientFullPOJO.getData().get(0).get_id(),ingredientFullPOJO.getData().get(1).get_id(),ingredientFullPOJO.getData().get(2).get_id()};
        Ingredient ingredientDef = new Ingredient(ingredientsString);

        ValidatableResponse responseCreateOrderWithoutAuth = orderApi.createOrderWithoutAuth(ingredientDef);

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

        //десериализуем ответ метода по получение ингредиентов и на его основе формируем тело для запроса по созданию заказа
        Response responseGetIngredient = given().get("https://stellarburgers.nomoreparties.site/api/ingredients");
        IngredientFullPOJO ingredientFullPOJO = responseGetIngredient.body().as(IngredientFullPOJO.class);
        String[] ingredientsString = {ingredientFullPOJO.getData().get(0).get_id(),ingredientFullPOJO.getData().get(1).get_id(),ingredientFullPOJO.getData().get(2).get_id()};
        Ingredient ingredientDef = new Ingredient(ingredientsString);

        ValidatableResponse responseCreateOrderWithAuth = orderApi.createOrderWithAuth(accessToken, ingredientDef);
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
