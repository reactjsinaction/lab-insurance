Feature: basic contract creation
  Scenario: insertar contrato basico
    When Inicializo la base de datos
    When Preparo contrato con acuerdo AM01
    When Establezco como suscriptor del contrato a la persona identificada con 11222333Z
    When Establezco como beneficiario del contrato a la persona identificada con 55666777W
    When Establezco un pago inicial neto de 100000 euros
    When Establezco la distribucion del pago inicial en (ASSET01:50%; ASSET02: 30%; GUARANTEE01:20%)
    When Establezco la fecha de contratacion a 2017/01/01
    When Muestro el JSON del contrato
    Then Invoco al servicio de contratacion
    And Recupero el numero del contrato
    And Recupero el contrato de base de datos a partir del numero
    And Verifico que el suscriptor es 11222333Z
