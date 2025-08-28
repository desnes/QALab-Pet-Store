Feature: Store - Gestión de Orders


  Background:
    Given defino la URL de Store "https://petstore.swagger.io/v2/"

  @order1 @TestEjecucion
  Scenario Outline: Crear una Order en Store (POST /store/order)
    When creo una orden con petId <petId>, quantity <quantity>, status "<status>" y complete "<complete>"
    Then el código de respuesta debe ser 200
    And el body de creación debe contener los campos enviados y un id válido

    Examples:
      | petId | quantity | status    | complete |
      | 10    | 2        | placed    | true     |
      | 11    | 1        | approved  | false    |

  @order2 @TestEjecucion
  Scenario: Consultar la Order creada (GET /store/order/{orderId})
    When consulto la orden por su id creado previamente
    Then el código de respuesta debe ser 200
    And el body de consulta debe coincidir con los datos de la creación
