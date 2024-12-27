package mizdooni.steps;

import io.cucumber.java.en.*;
import mizdooni.model.*;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class AddReservationSteps {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();

    private User findUser(String username){
        for (User tempUser : users)
            if (Objects.equals(tempUser.getUsername(), username))
                return tempUser;
        return null;
    }

    private Restaurant createRestaurant (String restaurantName){
        Address address = new Address("Iran", "Tehran", "Kargar");
        User manager = new User("Akbar Akbari", "password",
                "AkbarAkbari@example.com", address, User.Role.manager);
        String restaurantType = "Kababi";

        Restaurant restaurant = new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");

        return restaurant;
    }

    @Given("a user with username {string} and role {string}")
    public void aUserWithUsernameAndRole(String username, String role) {
        User tempUser = new User(username, "password123", username + "@example.com", null, User.Role.valueOf(role));
        users.add(tempUser);
    }

    @And("a new reservation for restaurant {string} at {string} by {string}")
    public void aNewReservationForRestaurantAt(String restaurantName, String dateTime, String username) {
        Restaurant restaurant = createRestaurant(restaurantName);
        Table table = new Table(1, restaurant.getId(), 8);
        
        User tempUser = findUser(username);
        Reservation reservation = new Reservation(tempUser, restaurant, table, LocalDateTime.parse(dateTime));
        reservation.setReservationNumber(reservations.size() + 1);
        reservations.add(reservation);
    }

    @When("all reservations are added")
    public void allReservationsAreAdded() {
        for (Reservation reservation : reservations)
            reservation.getUser().addReservation(reservation);
    }

    @Then("the {string} reservation count should be {string}")
    public void theReservationCountShouldBe(String username, String count) {
        int _count = Integer.parseInt(count);
        User tempUser = findUser(username);
        assertEquals(_count, tempUser.getReservations().size());
    }

    @And("the {string} reservation list is correct")
    public void theReservationListIsCorrect(String username) {
        User tempUser = findUser(username);
        int coveredCount = 0;
        for (Reservation reservation : reservations)
            if (reservation.getUser().equals(tempUser)) {
                assertEquals(reservation, tempUser.getReservation(reservation.getReservationNumber()));
                coveredCount++;
            }
        assertEquals(coveredCount, tempUser.getReservations().size());
    }
}
