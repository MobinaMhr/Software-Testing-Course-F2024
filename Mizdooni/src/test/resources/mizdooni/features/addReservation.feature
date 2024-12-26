Feature: Add Reservation
  As a user
  I want to add reservations
  So that I can manage my bookings

  Scenario: Adding a new reservation successfully
    Given a user with username "john_doe" and role "client"
    And a new reservation for restaurant "Pizza Place" at "2024-12-26T19:00"
    When the reservation is added
    Then the user's reservation count should be 1
    And the reservation list should contain the reservation for "Pizza Place"

  Scenario: Adding multiple reservations
    Given a user with username "jane_doe" and role "client"
    And a new reservation for restaurant "Burger Joint" at "2024-12-27T20:00"
    And a new reservation for restaurant "Sushi Spot" at "2024-12-28T18:00"
    When both reservations are added
    Then the user's reservation count should be 2
    And the reservation list should contain the reservation for "Burger Joint"
    And the reservation list should contain the reservation for "Sushi Spot"

  Scenario: Reservation number increments correctly
    Given a user with username "alice" and role "client"
    And a new reservation for restaurant "Steak House" at "2024-12-29T21:00"
    And another new reservation for restaurant "Deli Shop" at "2024-12-30T12:00"
    When both reservations are added
    Then the first reservation should have reservation number 0
    And the second reservation should have reservation number 1

  Scenario: Adding a duplicate reservation
    Given a user with username "john_doe" and role "client"
    And a new reservation for restaurant "Pizza Place" at "2024-12-26T19:00"
    When the reservation is added twice
    Then the user's reservation count should be 1
