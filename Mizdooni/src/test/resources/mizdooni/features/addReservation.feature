Feature: Add Reservation
  As a user
  I want to add reservations
  So that I can manage my bookings

  Scenario Outline: Adding a new reservation successfully
    Given a user with username "<username>" and role "<role>"
    And a new reservation for restaurant "<restaurant>" at "<datetime>" by "<username>"
    When all reservations are added
    Then the "<username>" reservation count should be "<reservation_count>"
    And the "<username>" reservation list is correct

    Examples:
      | username | role   | restaurant  | datetime         | reservation_count |
      | john_doe | client | Pizza Place | 2025-12-26T19:00 | 1                 |

  Scenario Outline: Adding multiple reservations
    Given a user with username "<username>" and role "<role>"
    And a new reservation for restaurant "<restaurant1>" at "<datetime1>" by "<username>"
    And a new reservation for restaurant "<restaurant2>" at "<datetime2>" by "<username>"
    When all reservations are added
    Then the "<username>" reservation count should be "<reservation_count>"
    And the "<username>" reservation list is correct

    Examples:
      | username | role   | restaurant1  | datetime1        | restaurant2 | datetime2        | reservation_count |
      | jane_doe | client | Burger Joint | 2025-12-27T20:00 | Sushi Spot  | 2025-12-28T18:00 | 2                 |

  Scenario Outline: Reservation number increments correctly
    Given a user with username "<username>" and role "<role>"
    And a new reservation for restaurant "<restaurant1>" at "<datetime1>" by "<username>"
    And a new reservation for restaurant "<restaurant2>" at "<datetime2>" by "<username>"
    When all reservations are added
    Then the "<username>" reservation count should be "<reservation_count>"
    And the "<username>" reservation list is correct

    Examples:
      | username | role   | restaurant1 | datetime1        | restaurant2 | datetime2        | reservation_count |
      | alice    | client | Steak House | 2025-12-29T21:00 | Deli Shop   | 2025-12-30T12:00 | 2                 |
