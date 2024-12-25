package mizdooni.steps;

import io.cucumber.java.en.*;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class AddReviewSteps {
    private Restaurant restaurant;
    private User user1;
    private User user2;
    private Review review1;
    private Review review2;

    @Given("a restaurant named {string} with no reviews")
    public void aRestaurantNamedWithNoReviews(String restaurantName) {
        User manager = new User("manager", "password123", "manager@example.com", null, User.Role.manager);
        restaurant = new Restaurant(restaurantName, manager, "Fine Dining", null, null, "Description", null, null);
    }

    @Given("a user named {string}")
    public void aUserNamed(String username) {
        if (user1 == null) {
            user1 = new User(username, "password123", username + "@example.com", null, User.Role.client);
        } else {
            user2 = new User(username, "password123", username + "@example.com", null, User.Role.client);
        }
    }

    @Given("a new review by {string} with ratings food {int}, service {int}, ambiance {int}, overall {int}")
    public void aNewReviewByWithRatingsFoodServiceAmbianceOverall(String username, int food_, int service_, int ambiance_, int overall_) {
        User user = username.equals(user1.getUsername()) ? user1 : user2;
        Rating rating = new Rating(){{food = food_; ambiance = ambiance_; service = service_; overall= overall_;}};
        Review review = new Review(user, rating, "Great food!", LocalDateTime.now());
        
        if (review1 == null) {
            review1 = review;
        } else {
            review2 = review;
        }
    }

    @Given("a restaurant named {string} with an existing review by {string} with ratings food {int}, service {int}, ambiance {int}, overall {int}")
    public void aRestaurantNamedWithAnExistingReviewByWithRatingsFoodServiceAmbianceOverall(String restaurantName, String username, int food, int service, int ambiance, int overall) {
        aRestaurantNamedWithNoReviews(restaurantName);
        aUserNamed(username);
        aNewReviewByWithRatingsFoodServiceAmbianceOverall(username, food, service, ambiance, overall);
        restaurant.addReview(review1);
    }

    @When("the review is added to the restaurant")
    public void theReviewIsAddedToTheRestaurant() {
        restaurant.addReview(review1);
    }

    @When("both reviews are added to the restaurant")
    public void bothReviewsAreAddedToTheRestaurant() {
        restaurant.addReview(review1);
        restaurant.addReview(review2);
    }

    @Then("the restaurant should have {int} review(s)")
    public void theRestaurantShouldHaveReviewCount(int count) {
        assertEquals(count, restaurant.getReviews().size());
    }

    @Then("the average rating should be food {double}, service {double}, ambiance {double}, overall {double}")
    public void theAverageRatingShouldBeFoodServiceAmbianceOverall(double food, double service, double ambiance, double overall) {
        Rating average = restaurant.getAverageRating();
        assertEquals(food, average.food, 0.01);
        assertEquals(service, average.service, 0.01);
        assertEquals(ambiance, average.ambiance, 0.01);
        assertEquals(overall, average.overall, 0.01);
    }
}
