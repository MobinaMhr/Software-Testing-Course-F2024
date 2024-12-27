Feature: Calculate Average Rating
  As a restaurant manager
  I want to calculate the average rating of my restaurant
  So that I can evaluate customer feedback

  Scenario: No reviews
    Given a restaurant named "Green Bistro" managed by user "manager1"
    When the average rating is calculated
    Then the average rating should be 0 for all categories

  Scenario Outline: Single review
    Given a restaurant named "Green Bistro" managed by user "manager1"
    And a review by user "<user>" with food rating <food>, service rating <service>, ambiance rating <ambiance>, and overall rating <overall>
    When the average rating is calculated
    Then the average rating should be food <food>, service <service>, ambiance <ambiance>, and overall <overall>

    Examples:
      | user    | food | service | ambiance | overall |
      | client1 | 4    | 5       | 4        | 4       |

  Scenario Outline: Multiple reviews
    Given a restaurant named "Green Bistro" managed by user "manager1"
    And a review by user "<user1>" with food rating <food1>, service rating <service1>, ambiance rating <ambiance1>, and overall rating <overall1>
    And a review by user "<user2>" with food rating <food2>, service rating <service2>, ambiance rating <ambiance2>, and overall rating <overall2>
    When the average rating is calculated
    Then the average rating should be food <avg_food>, service <avg_service>, ambiance <avg_ambiance>, and overall <avg_overall>

    Examples:
      | user1   | food1 | service1 | ambiance1 | overall1 | user2   | food2 | service2 | ambiance2 | overall2 | avg_food | avg_service | avg_ambiance | avg_overall |
      | client1 | 4     | 5        | 4         | 4        | client2 | 3     | 4        | 5         | 3        | 3.5      | 4.5         | 4.5          | 3.5         |

  Scenario Outline: Updating a user's review
    Given a restaurant named "Green Bistro" managed by user "manager1"
    And a review by user "<user>" with food rating <initial_food>, service rating <initial_service>, ambiance rating <initial_ambiance>, and overall rating <initial_overall>
    And a review by user "<user>" with food rating <updated_food>, service rating <updated_service>, ambiance rating <updated_ambiance>, and overall rating <updated_overall>
    When the average rating is calculated
    Then the average rating should be food <avg_food>, service <avg_service>, ambiance <avg_ambiance>, and overall <avg_overall>

    Examples:
      | user    | initial_food | initial_service | initial_ambiance | initial_overall | updated_food | updated_service | updated_ambiance | updated_overall | avg_food | avg_service | avg_ambiance | avg_overall |
      | client1 | 4            | 5               | 4                | 4               | 2            | 3               | 2                | 2               | 2        | 3           | 2            | 2           |
