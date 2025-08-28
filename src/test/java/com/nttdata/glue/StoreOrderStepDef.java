package com.nttdata.glue;

import com.nttdata.steps.StoreOrderStep;
import io.cucumber.java.en.*;
import net.thucydides.core.annotations.Steps;

public class StoreOrderStepDef {

    @Steps
    StoreOrderStep store;

    @Given("defino la URL de Store {string}")
    public void definoLaURLDeStore(String url) {
        store.setBaseUrl(url);
    }

    @When("creo una orden con petId {int}, quantity {int}, status {string} y complete {string}")
    public void creoOrden(int petId, int quantity, String status, String completeStr) {
        boolean complete = Boolean.parseBoolean(completeStr);
        store.createOrder(petId, quantity, status, complete);
    }

    @Then("el código de respuesta debe ser {int}")
    public void codigoRespuestaDebeSer(int status) {
        if (status == 200) {
            // Se usa en ambos escenarios. Detecto por último request ejecutado.
            store.validateCreateStatusCode200(); // POST
        } else {
            throw new AssertionError("Sólo se ha modelado validación 200 en este ejemplo.");
        }
    }

    @Then("el body de creación debe contener los campos enviados y un id válido")
    public void bodyCreacionValido() {
        store.validateCreateBody();
    }

    @When("consulto la orden por su id creado previamente")
    public void consultoOrdenPorIdPrevio() {
        store.getOrderByCreatedId();
    }

    @Then("el body de consulta debe coincidir con los datos de la creación")
    public void bodyConsultaCoincide() {
        store.validateGetStatusCode200();
        store.validateGetBodyMatchesCreation();
    }
}
