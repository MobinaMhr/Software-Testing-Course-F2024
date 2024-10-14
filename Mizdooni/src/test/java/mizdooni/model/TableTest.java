package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class TableTest {
    private Restaurant restaurant;
    private Table table;
    private Reservation reservation1;
    private Reservation reservation2;
    private User manager;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        Address address = new Address("Iran", "Tehran", "Kargar");
        manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address,
                User.Role.manager);
        user1 = new User("mmd", "password", "mmd@example.com", address, User.Role.client);
        user2 = new User("mobina", "password", "mobina@example.com", address, User.Role.client);
        restaurant = new Restaurant("Baradaran Akbari bejoz Davood", manager, "Kababi", LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");
        table = new Table(1, restaurant.getId(), 4);
        reservation1 = new Reservation(user1, restaurant, table, LocalDateTime.now());
        reservation2 = new Reservation(user2, restaurant, table, LocalDateTime.now().plusHours(1));
    }

    @Test
    public void testAddSingleReservation(){
        table.addReservation(reservation1);
        assertEquals(1, table.getReservations().size());
        assertEquals(reservation1, table.getReservations().getFirst());
    }//TODO:ask if we should check the time of reservation and prevent reservations at same time for a table or not?

    @Test
    public void testAddMultipleReservations(){
        table.addReservation(reservation1);
        table.addReservation(reservation2);
        assertEquals(2, table.getReservations().size());
        assertEquals(reservation1, table.getReservations().getFirst());
        assertEquals(reservation2, table.getReservations().get(1));
    }
}
