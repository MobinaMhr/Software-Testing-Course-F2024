Feature: Add Review
  As a restaurant customer
  I want to add reviews for a restaurant
  So that I can share my feedback

  Scenario: Adding a new review
    Given a restaurant named "Gourmet Hub" with no reviews
    And a user named "john_doe"
    And a new review by "john_doe" with ratings food 5, service 4, ambiance 4, overall 5
    When the review is added to the restaurant
    Then the restaurant should have 1 review
    And the average rating should be food 5.0, service 4.0, ambiance 4.0, overall 5.0

  Scenario: Replacing an existing review by the same user
    Given a restaurant named "Gourmet Hub" with an existing review by "john_doe" with ratings food 4, service 3, ambiance 3, overall 4
    And a new review by "john_doe" with ratings food 5, service 5, ambiance 5, overall 5
    When the review is added to the restaurant
    Then the restaurant should still have 1 review
    And the average rating should be food 5.0, service 5.0, ambiance 5.0, overall 5.0

  Scenario: Adding multiple reviews by different users
    Given a restaurant named "Gourmet Hub" with no reviews
    And a user named "john_doe"
    And a user named "jane_doe"
    And a new review by "john_doe" with ratings food 5, service 4, ambiance 4, overall 5
    And a new review by "jane_doe" with ratings food 3, service 4, ambiance 3, overall 3
    When both reviews are added to the restaurant
    Then the restaurant should have 2 reviews
    And the average rating should be food 4.0, service 4.0, ambiance 3.5, overall 4.0
