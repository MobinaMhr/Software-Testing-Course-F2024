package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {
    private Restaurant restaurant;
    private User manager;
    Table table1;
    Table table2;
    private String restaurantName;
    private String restaurantType;
    Address address;

    @BeforeEach
    public void setUp(){
        address = new Address("Iran", "Tehran", "Kargar");
        manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address,
                User.Role.manager);
        //user1 = new User("mmd", "password", "mmd@example.com", address, User.Role.client);
        //user2 = new User("mobina", "password", "mobina@example.com", address, User.Role.client);
        restaurantName = "Baradaran Akbari bejoz Davood";
        restaurantType = "Kababi";

        restaurant = new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");
        table1 = new Table(-1, restaurant.getId(), 2);
        table2 = new Table(-1, restaurant.getId(), 4);
    }

    @Test
    @DisplayName("Test Correct Construction of Restaurant")
    public void testCorrectConstructionOfRestaurant(){
        assertEquals(restaurantName, restaurant.getName());
        assertEquals(manager, restaurant.getManager());
        assertEquals(restaurantType, restaurant.getType());
        assertEquals(address, restaurant.getAddress());
    }

    @Test
    @DisplayName("Test Adding Single Table")
    public void testAddingSingleTable(){
        restaurant.addTable(table1);
        assertEquals(1, restaurant.getTables().size());
        assertEquals(1, table1.getTableNumber());
        assertEquals(table1, restaurant.getTables().getFirst());
    }

    @Test
    @DisplayName("Test Adding Multiple Tables")
    public void testAddingMultipleTables(){
        restaurant.addTable(table1);
        restaurant.addTable(table2);
        assertEquals(2, restaurant.getTables().size());
        assertEquals(1, table1.getTableNumber());
        assertEquals(2, table2.getTableNumber());
        assertEquals(table1, restaurant.getTables().getFirst());
        assertEquals(table2, restaurant.getTables().get(1));
    }

    @Test
    @DisplayName("Test Getting Table from Empty Restaurant")
    public void testGettingTableFromEmptyRestaurant(){
        assertEquals(0, restaurant.getTables().size());
        assertNull(restaurant.getTable(1));
    }

    @Test
    @DisplayName("Test Getting non Existing Table")
    public void testGettingNonExistingTable(){
        restaurant.addTable(table1);
        assertNull(restaurant.getTable(5));
    }

    @Test
    @DisplayName("Test Getting Table for Existing Table")
    public void testGettingTableForExistingTable(){
        restaurant.addTable(table1);
        assertEquals(table1, restaurant.getTable(1));
    }

    @Test
    @DisplayName("Test Getting Max Seats Number")
    public void testGettingMaxSeatsNumber(){
        restaurant.addTable(table1);
        restaurant.addTable(table2);
        assertEquals(4, restaurant.getMaxSeatsNumber());
    }

    @Test
    @DisplayName("Test Getting Correct Seat Count")
    public void testGettingCorrectSeatCount() {
        assertEquals(1, restaurant.getMaxSeatsNumber());
    }
}