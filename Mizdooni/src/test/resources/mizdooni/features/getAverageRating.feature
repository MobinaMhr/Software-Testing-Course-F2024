Feature: Calculate Average Rating
  As a restaurant manager
  I want to calculate the average rating of my restaurant
  So that I can evaluate customer feedback

  Scenario: No reviews
    Given a restaurant named "Green Bistro" managed by user "manager1"
    When the average rating is calculated
    Then the average rating should be 0 for all categories

  Scenario: Single review
    Given a restaurant named "Green Bistro" managed by user "manager1"
    And a review by user "client1" with food rating 4, service rating 5, ambiance rating 4, and overall rating 4
    When the average rating is calculated
    Then the average rating should be food 4.0, service 5.0, ambiance 4.0, and overall 4.0

  Scenario: Multiple reviews
    Given a restaurant named "Green Bistro" managed by user "manager1"
    And a review by user "client1" with food rating 4, service rating 5, ambiance rating 4, and overall rating 4
    And a review by user "client2" with food rating 3, service rating 4, ambiance rating 5, and overall rating 3
    When the average rating is calculated
    Then the average rating should be food 3.5, service 4.5, ambiance 4.5, and overall 3.5

  Scenario: Updating a user's review
    Given a restaurant named "Green Bistro" managed by user "manager1"
    And a review by user "client1" with food rating 4, service rating 5, ambiance rating 4, and overall rating 4
    And another review by user "client1" with food rating 2, service rating 3, ambiance rating 2, and overall rating 2
    When the average rating is calculated
    Then the average rating should be food 2.0, service 3.0, ambiance 2.0, and overall 2.0
