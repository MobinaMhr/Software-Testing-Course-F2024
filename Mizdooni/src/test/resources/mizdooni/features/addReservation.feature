Feature: Add Reservation
  As a user
  I want to add reservations
  So that I can book tables at restaurants

  Scenario: Adding a reservation successfully
    Given a user with username "john_doe" and email "john@example.com"
    And a restaurant named "Fine Dine"
    When the user adds a reservation for the restaurant
    Then the user should have 1 reservation
    And the reservation should belong to the restaurant "Fine Dine"

  Scenario: Adding multiple reservations
    Given a user with username "jane_doe" and email "jane@example.com"
    And a restaurant named "Gourmet Palace"
    When the user adds 3 reservations for the restaurant
    Then the user should have 3 reservations

  Scenario: Checking if a user has reserved a specific restaurant
    Given a user with username "alice" and email "alice@example.com"
    And a restaurant named "Bistro"
    When the user adds a reservation for the restaurant
    And the user checks if they have reserved the restaurant "Bistro"
    Then the result should be true

  Scenario: Checking if a user has no reservations for a restaurant
    Given a user with username "bob" and email "bob@example.com"
    And a restaurant named "Cafe Delight"
    When the user checks if they have reserved the restaurant "Cafe Delight"
    Then the result should be false
