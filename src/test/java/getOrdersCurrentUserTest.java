import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class getOrdersCurrentUserTest {

    static OrderApi orderApi;
    static UserApi userApi;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        userApi = new UserApi();
    }

    @Test
    @DisplayName("Получение списка заказов пользователя с авторизацией")
    @Description("Проверка получение списка заказов пользователя с авторизацией")
    public void getOrdersUserWithAuth() {
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

        String nameOrder = responseCreateOrderWithAuth.extract().path("name");

        ValidatableResponse responseGetOrdersUser = orderApi.getOrderCurrentUserWithAuth(accessToken);

        Response response = given().auth().oauth2(accessToken).get("https://stellarburgers.nomoreparties.site/api/orders");
        OrdersFullPOJO ordersFullPOJO = response.body().as(OrdersFullPOJO.class);

        List<OrdersPOJO> ordersPOJO = ordersFullPOJO.getOrders();

        int statusCode = responseGetOrdersUser.extract().statusCode();
        boolean successActual = responseGetOrdersUser.extract().path("success");
        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertTrue(successActual);

        Assert.assertEquals(nameOrder, ordersPOJO.get(0).getName());
        Assert.assertTrue(Arrays.equals(ingredientDef.getIngredients(), ordersPOJO.get(0).getIngredients()));

        userApi.delete(accessToken);
    }

    @Test
    @DisplayName("Получение списка заказов пользователя без авторизацией")
    @Description("Проверка получение списка заказов пользователя без авторизацией")
    public void getOrdersUserWithoutAuth() {
        ValidatableResponse responseGetOrdersUser = orderApi.getOrderCurrentUserWithoutAuth();
        int statusCode = responseGetOrdersUser.extract().statusCode();
        boolean successActual = responseGetOrdersUser.extract().path("success");
        String messageActual = responseGetOrdersUser.extract().path("message");

        Assert.assertEquals(SC_UNAUTHORIZED, statusCode);
        Assert.assertFalse(successActual);
        Assert.assertEquals("You should be authorised", messageActual);
    }
}
