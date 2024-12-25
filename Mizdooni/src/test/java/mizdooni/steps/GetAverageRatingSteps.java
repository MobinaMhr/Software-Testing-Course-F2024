package mizdooni.steps;

import io.cucumber.java.en.*;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GetAverageRatingSteps {
    private Restaurant restaurant;
    private Rating averageRating;
    private Map<String, User> users = new HashMap<>();

    @Given("a restaurant named {string} managed by user {string}")
    public void aRestaurantNamedManagedByUser(String restaurantName, String managerUsername) {
        User manager = users.computeIfAbsent(managerUsername, k -> new User(managerUsername, "password123", managerUsername + "@example.com", null, User.Role.manager));
        restaurant = new Restaurant(restaurantName, manager, "Fine Dining", null, null, "A great place", null, null);
    }

    @And("a review by user {string} with food rating {int}, service rating {int}, ambiance rating {int}, and overall rating {int}")
    public void aReviewByUserWithRatings(String username, int food_, int service_, int ambiance_, int overall_) {
        User user = users.computeIfAbsent(username, k -> new User(username, "password123", username + "@example.com", null, User.Role.client));
        Rating rating = new Rating(){{food = food_; ambiance = ambiance_; service = service_; overall= overall_;}};
        Review review = new Review(user, rating, "Great food!", LocalDateTime.now());
        restaurant.addReview(review);
    }

    @When("the average rating is calculated")
    public void theAverageRatingIsCalculated() {
        averageRating = restaurant.getAverageRating();
    }

    @Then("the average rating should be food {double}, service {double}, ambiance {double}, and overall {double}")
    public void theAverageRatingShouldBe(Double food, Double service, Double ambiance, Double overall) {
        assertEquals(food, averageRating.food, 0.01);
        assertEquals(service, averageRating.service, 0.01);
        assertEquals(ambiance, averageRating.ambiance, 0.01);
        assertEquals(overall, averageRating.overall, 0.01);
    }

    @Then("the average rating should be 0 for all categories")
    public void theAverageRatingShouldBeZeroForAllCategories() {
        assertEquals(0.0, averageRating.food, 0.01);
        assertEquals(0.0, averageRating.service, 0.01);
        assertEquals(0.0, averageRating.ambiance, 0.01);
        assertEquals(0.0, averageRating.overall, 0.01);
    }
}
