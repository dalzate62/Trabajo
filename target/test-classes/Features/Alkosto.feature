Feature: Entrevista Practica
  optional description of the feature

  @AgregarProducto
  Scenario: Adjuntar articulo al Carro
    Given Sitio sin parametro
    Then I Load The DOM Information alkosto.json
    And I Do Click In Element Telefonos
    And I Do Click In Element SeleccionaTelefonos
    And I Do Click In Element AgregarProducto
    And I Do Click In Element ContinuarComprando

