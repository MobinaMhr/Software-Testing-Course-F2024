package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private Address address;
    private User user;
    private User clientUser;
    private User managerUser;

    Restaurant restaurant1;
    Restaurant restaurant2;
    Restaurant restaurant3;
    Restaurant restaurant4;

    Table table1;
    Table table2;
    Table table3;

    @BeforeEach
    public void setUp() {
        address = new Address("Iran", "Tehran", "Kargar");

        restaurant1 = new Restaurant("restaurant1", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant1 description.", address, "imageLink");

        restaurant2 = new Restaurant("restaurant2", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant2 description.", address, "imageLink");

        restaurant3 = new Restaurant("restaurant3", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant3 description.", address, "imageLink");

        restaurant4 = new Restaurant("restaurant4", managerUser,
                "restaurantType", LocalTime.now(), LocalTime.now(),
                "restaurant4 description.", address, "imageLink");

        Table table1 = new Table(1, restaurant1.getId(), 8);
        Table table2 = new Table(1, restaurant1.getId(), 8);
        Table table3 = new Table(1, restaurant3.getId(), 8);


        user = new User("testUser", "testPassword", "test@example.com", address, User.Role.client);

        clientUser = new User("client", "cPass", "client@example.com", address, User.Role.client);
        managerUser = new User("manager", "mPass", "manager@example.com", address, User.Role.manager);
    }

    @Test
    @DisplayName("Test Correct Construction of User")
    public void testCorrectConstructionOfUser(){
        assertEquals("testUser", user.getUsername());
        assertTrue(user.checkPassword("testPassword"));
        assertEquals("test@example.com", user.getEmail());
        assertEquals(user.getRole(), User.Role.client);

        assertEquals(0, clientUser.getReservations().size());
    }

    @Test
    @DisplayName("Test Checking Wrong Username")
    public void testCheckingWrongUsername() {
        assertNotEquals("wrongTestUser", user.getUsername());
    }

    @Test
    @DisplayName("Test Checking Wrong Password")
    public void testCheckingWrongPassword() {
        assertFalse(user.checkPassword("wrongPassword"));
    }

    @Test
    @DisplayName("Test Checking Wrong Email")
    public void testCheckingWrongEmail() {
        assertNotEquals("wrongTest@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Test Checking Wrong Role")
    public void testCheckingWrongRole() {
        assertNotEquals(User.Role.manager, user.getRole());
    }

    @Test
    @DisplayName("Test Checking Wrong Reservation Count")
    public void testCheckingWrongReservationCount() {
        assertNotEquals(1, clientUser.getReservations().size());
    }

    @Test
    @DisplayName("Test Checking Ongoing Users Reservation")
    public void testCheckingOngoingUsersReservation() {
        Reservation reservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());

        clientUser.addReservation(reservation);

        assertTrue(clientUser.checkReserved(restaurant1));
    }

    @Test
    @DisplayName("Test Getting Canceled Reservation")
    public void testGettingCanceledReservation() {
        Reservation reservation = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());

        clientUser.addReservation(reservation);
        clientUser.getReservation(reservation.getReservationNumber()).cancel();

        assertFalse(clientUser.checkReserved(restaurant1));
    }

    @Test
    @DisplayName("Test Checking Multiple Ongoing Reservations")
    public void testCheckingMultipleOngoingReservations() {
        Reservation reservation1 = new Reservation(clientUser, restaurant1, table1, LocalDateTime.now());
        Reservation reservation2 = new Reservation(clientUser, restaurant1, table2, LocalDateTime.now());
        Reservation reservation3 = new Reservation(clientUser, restaurant3, table3, LocalDateTime.now());

        clientUser.addReservation(reservation1);
        clientUser.addReservation(reservation2);
        clientUser.addReservation(reservation3);

        Assertions.assertEquals(3, clientUser.getReservations().size());

        Assertions.assertEquals(reservation1, clientUser.getReservation(reservation1.getReservationNumber()));
        Assertions.assertEquals(reservation2, clientUser.getReservation(reservation2.getReservationNumber()));
        Assertions.assertEquals(reservation3, clientUser.getReservation(reservation3.getReservationNumber()));
    }
}
