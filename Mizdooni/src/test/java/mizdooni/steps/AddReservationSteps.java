package mizdooni.steps;

import io.cucumber.java.en.*;
import mizdooni.model.*;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddReservationSteps {
    private User user;
    private Reservation reservation1;
    private Reservation reservation2;

    @Given("a user with username {string} and role {string}")
    public void aUserWithUsernameAndRole(String username, String role) {
        user = new User(username, "password123", username + "@example.com", null, User.Role.valueOf(role));
    }

    @And("a new reservation for restaurant {string} at {string}")
    public void aNewReservationForRestaurantAt(String restaurantName, String dateTime) {

        Address address = new Address("Iran", "Tehran", "Kargar");
        User manager = new User("Akbar Akbari", "password",
                "AkbarAkbari@example.com", address, User.Role.manager);
        String restaurantType = "Kababi";

        Restaurant restaurant = new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");

        Table table = new Table(1, restaurant.getId(), 8);
        reservation1 = new Reservation(user, restaurant, table, LocalDateTime.parse(dateTime));
    }

    @And("another new reservation for restaurant {string} at {string}")
    public void anotherNewReservationForRestaurantAt(String restaurantName, String dateTime) {
        Address address = new Address("Iran", "Tehran", "Kargar");
        User manager = new User("Akbar Akbari", "password",
                "AkbarAkbari@example.com", address, User.Role.manager);
        String restaurantType = "Kababi";

        Restaurant restaurant = new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");

        Table table = new Table(1, restaurant.getId(), 8);
        reservation2 = new Reservation(user, restaurant, table, LocalDateTime.parse(dateTime));
    }

    @When("the reservation is added")
    public void theReservationIsAdded() {
        user.addReservation(reservation1);
    }

    @When("both reservations are added")
    public void bothReservationsAreAdded() {
        user.addReservation(reservation1);
        user.addReservation(reservation2);
    }

    @Then("the user's reservation count should be {int}")
    public void theUsersReservationCountShouldBe(int count) {
        assertEquals(count, user.getReservations().size());
    }

    @Then("the reservation list should contain the reservation for {string}")
    public void theReservationListShouldContainTheReservationFor(String restaurantName) {
        assertTrue(user.getReservations().stream()
                .anyMatch(r -> r.getRestaurant().getName().equals(restaurantName)));
    }

    @Then("the first reservation should have reservation number {int}")
    public void theFirstReservationShouldHaveReservationNumber(int reservationNumber) {
        assertEquals(reservationNumber, user.getReservations().getFirst().getReservationNumber());
    }

    @Then("the second reservation should have reservation number {int}")
    public void theSecondReservationShouldHaveReservationNumber(int reservationNumber) {
        assertEquals(reservationNumber, user.getReservations().get(1).getReservationNumber());
    }
}
