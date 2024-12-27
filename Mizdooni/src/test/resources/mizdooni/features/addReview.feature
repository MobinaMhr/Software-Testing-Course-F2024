Feature: Add Review
  As a restaurant customer
  I want to add reviews for a restaurant
  So that I can share my feedback

  Scenario Outline: Adding a new review
    Given a restaurant named "Gourmet Hub" with no reviews
    And a user named "<user>"
    And a new review by "<user>" with ratings food <food>, service <service>, ambiance <ambiance>, overall <overall>
    When the review is added to the restaurant
    Then the restaurant should have <review_count> review
    And the average rating should be food <avg_food>, service <avg_service>, ambiance <avg_ambiance>, overall <avg_overall>

    Examples:
      | user     | food | service | ambiance | overall | review_count | avg_food | avg_service | avg_ambiance | avg_overall |
      | john_doe | 5    | 4       | 4        | 5       | 1            | 5.0      | 4.0         | 4.0          | 5.0         |

  Scenario Outline: Replacing an existing review by the same user
    Given a restaurant named "Gourmet Hub" with an existing review by "<user>" with ratings food <initial_food>, service <initial_service>, ambiance <initial_ambiance>, overall <initial_overall>
    And a new review by "<user>" with ratings food <updated_food>, service <updated_service>, ambiance <updated_ambiance>, overall <updated_overall>
    When the review is added to the restaurant
    Then the restaurant should have <review_count> review
    And the average rating should be food <avg_food>, service <avg_service>, ambiance <avg_ambiance>, overall <avg_overall>

    Examples:
      | user     | initial_food | initial_service | initial_ambiance | initial_overall | updated_food | updated_service | updated_ambiance | updated_overall | review_count | avg_food | avg_service | avg_ambiance | avg_overall |
      | john_doe | 4            | 3               | 3                | 4               | 5            | 5               | 5                | 5               | 1            | 5.0      | 5.0         | 5.0          | 5.0         |

  Scenario Outline: Adding multiple reviews by different users
    Given a restaurant named "Gourmet Hub" with no reviews
    And a user named "<user1>"
    And a user named "<user2>"
    And a new review by "<user1>" with ratings food <food1>, service <service1>, ambiance <ambiance1>, overall <overall1>
    And a new review by "<user2>" with ratings food <food2>, service <service2>, ambiance <ambiance2>, overall <overall2>
    When both reviews are added to the restaurant
    Then the restaurant should have <review_count> reviews
    And the average rating should be food <avg_food>, service <avg_service>, ambiance <avg_ambiance>, overall <avg_overall>

    Examples:
      | user1    | food1 | service1 | ambiance1 | overall1 | user2    | food2 | service2 | ambiance2 | overall2 | review_count | avg_food | avg_service | avg_ambiance | avg_overall |
      | john_doe | 5     | 4        | 4         | 5        | jane_doe | 3     | 4        | 3         | 3        | 2            | 4.0      | 4.0         | 3.5          | 4.0         |
