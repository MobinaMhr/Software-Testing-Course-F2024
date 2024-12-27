Feature: Add Reservation
  As a user
  I want to add reservations
  So that I can manage my bookings

  Scenario Outline: Adding a new reservation successfully
    Given a user with username "<username>" and role "<role>"
    And a new reservation for restaurant "<restaurant>" at "<datetime>"
    When the reservation is added
    Then the user's reservation count should be <reservation_count>
    And the reservation list should contain the reservation for "<restaurant>"

    Examples:
      | username | role   | restaurant  | datetime         | reservation_count |
      | john_doe | client | Pizza Place | 2025-12-26T19:00 | 1                 |

  Scenario Outline: Adding multiple reservations
    Given a user with username "<username>" and role "<role>"
    And a new reservation for restaurant "<restaurant1>" at "<datetime1>"
    And a new reservation for restaurant "<restaurant2>" at "<datetime2>"
    When both reservations are added
    Then the user's reservation count should be <reservation_count>
    And the reservation list should contain the reservation for "<restaurant1>"
    And the first reservation should have reservation number 0
    And the reservation list should contain the reservation for "<restaurant2>"
    And the second reservation should have reservation number 1

    Examples:
      | username | role   | restaurant1  | datetime1        | restaurant2 | datetime2        | reservation_count |
      | jane_doe | client | Burger Joint | 2025-12-27T20:00 | Sushi Spot  | 2025-12-28T18:00 | 2                 |

  Scenario Outline: Reservation number increments correctly
    Given a user with username "<username>" and role "<role>"
    And a new reservation for restaurant "<restaurant1>" at "<datetime1>"
    And another new reservation for restaurant "<restaurant2>" at "<datetime2>"
    When both reservations are added
    Then the first reservation should have reservation number 0
    And the second reservation should have reservation number 1

    Examples:
      | username | role   | restaurant1 | datetime1        | restaurant2 | datetime2        |
      | alice    | client | Steak House | 2025-12-29T21:00 | Deli Shop   | 2025-12-30T12:00 |
