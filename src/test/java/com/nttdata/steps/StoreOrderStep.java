package com.nttdata.steps;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StoreOrderStep {

    private String baseUrl;
    private Response response;

    // Estado compartido entre escenarios del mismo Feature
    // (en la misma JVM de ejecución)
    public static Long lastOrderId;             // id generado en el POST
    public static Map<String, Object> lastOrder;// payload usado/retornado

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

    public void createOrder(int petId, int quantity, String status, boolean complete) {
        // shipDate opcional: envío un ISO-8601 válido
        String shipDate = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Map<String, Object> payload = new HashMap<>();
        // Puedes omitir id para que el server asigne, pero la PetStore permite enviar uno.
        // Lo dejamos nulo para que el backend genere:
        payload.put("id", null);
        payload.put("petId", petId);
        payload.put("quantity", quantity);
        payload.put("shipDate", shipDate);
        payload.put("status", status);
        payload.put("complete", complete);

        response = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(payload)
                .log().all()
                .when()
                .post("/store/order")
                .then()
                .log().all()
                .extract().response();

        // Guardar estado de creación para el segundo escenario
        lastOrder = new HashMap<>(payload);
        // Tomar el id real retornado
        lastOrderId = response.jsonPath().getLong("id");
    }

    public void validateCreateStatusCode200() {
        Assert.assertEquals("Status code creación", 200, response.getStatusCode());
    }

    public void validateCreateBody() {
        // id generado
        Long id = response.jsonPath().getLong("id");
        Assert.assertNotNull("id generado no debe ser null", id);

        // Validar eco de campos
        Integer petIdResp = response.jsonPath().getInt("petId");
        Integer quantityResp = response.jsonPath().getInt("quantity");
        String statusResp = response.jsonPath().getString("status");
        Boolean completeResp = response.jsonPath().getBoolean("complete");

        Assert.assertEquals("petId", lastOrder.get("petId"), petIdResp);
        Assert.assertEquals("quantity", lastOrder.get("quantity"), quantityResp);
        Assert.assertEquals("status", lastOrder.get("status"), statusResp);
        Assert.assertEquals("complete", lastOrder.get("complete"), completeResp);
    }

    public void getOrderByCreatedId() {
        Assert.assertNotNull("No hay orderId previo. El POST no se ejecutó o falló.", lastOrderId);

        response = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .log().all()
                .when()
                .get("/store/order/" + lastOrderId)
                .then()
                .log().all()
                .extract().response();
    }

    public void validateGetStatusCode200() {
        Assert.assertEquals("Status code consulta", 200, response.getStatusCode());
    }

    public void validateGetBodyMatchesCreation() {
        // El body del GET debe coincidir con lo creado
        Long id = response.jsonPath().getLong("id");
        Integer petIdResp = response.jsonPath().getInt("petId");
        Integer quantityResp = response.jsonPath().getInt("quantity");
        String statusResp = response.jsonPath().getString("status");
        Boolean completeResp = response.jsonPath().getBoolean("complete");

        Assert.assertEquals("id", lastOrderId, id);
        Assert.assertEquals("petId", lastOrder.get("petId"), petIdResp);
        Assert.assertEquals("quantity", lastOrder.get("quantity"), quantityResp);
        Assert.assertEquals("status", lastOrder.get("status"), statusResp);
        Assert.assertEquals("complete", lastOrder.get("complete"), completeResp);
    }
}
