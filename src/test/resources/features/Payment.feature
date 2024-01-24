Feature: Payment Service

  Scenario: Get order by ID
    Given Entity
    Given I have an order with ID 1
    Then Save order Repository
    When I request the order by this ID
    Then I should receive the order details
