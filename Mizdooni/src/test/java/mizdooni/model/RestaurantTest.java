package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {
    private Restaurant restaurant;
    private User manager;

    @BeforeEach
    public void setUp(){
        Address address = new Address("Iran", "Tehran", "Kargar");
        manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address,
                User.Role.manager);
        //user1 = new User("mmd", "password", "mmd@example.com", address, User.Role.client);
        //user2 = new User("mobina", "password", "mobina@example.com", address, User.Role.client);
        restaurant = new Restaurant("Baradaran Akbari bejoz Davood", manager, "Kababi", LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");

    }

    @Test
    public void testAddSingleTable(){
        Table table = new Table(-1, restaurant.getId(), 2);
        restaurant.addTable(table);
        assertEquals(1, restaurant.getTables().size());
        assertEquals(1, table.getTableNumber());
        assertEquals(table, restaurant.getTables().getFirst());
    }
}
