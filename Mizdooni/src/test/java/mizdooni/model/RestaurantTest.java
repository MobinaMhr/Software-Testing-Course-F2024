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

//    public Restaurant(String name, User manager, String type, LocalTime startTime, LocalTime endTime,
//                      String description, Address address, String imageLink) {
//        this.id = idCounter++;
//        this.name = name;
//        this.manager = manager;
//        this.type = type;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.description = description;
//        this.address = address;
//        this.imageLink = imageLink;
//        this.tables = new ArrayList<>();
//        this.reviews = new ArrayList<>();
//    }


    @Test
    @DisplayName("Test Adding Single Table")
    public void testAddingSingleTable(){
        Table table = new Table(-1, restaurant.getId(), 2);
        restaurant.addTable(table);
        assertEquals(1, restaurant.getTables().size());
        assertEquals(1, table.getTableNumber());
        assertEquals(table, restaurant.getTables().getFirst());
    }

    @Test
    @DisplayName("Test Adding Multiple Tables")
    public void testAddingMultipleTables(){
        Table table1 = new Table(-1, restaurant.getId(), 2);
        Table table2 = new Table(-1, restaurant.getId(), 4);
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
        assertNull(restaurant.getTable(1));
    }

    @Test
    @DisplayName("Test Getting non Existing Table")
    public void testGettingNonExistingTable(){
        Table table1 = new Table(-1, restaurant.getId(), 2);
        restaurant.addTable(table1);
        assertNull(restaurant.getTable(5));
    }

    @Test
    @DisplayName("Test Getting Table for Existing Table")
    public void testGettingTableForExistingTable(){
        Table table1 = new Table(-1, restaurant.getId(), 2);
        restaurant.addTable(table1);
        assertEquals(table1, restaurant.getTable(1));
    }

    @Test
    @DisplayName("Test Getting Max Seats Number")
    public void testGettingMaxSeatsNumber(){
        Table table1 = new Table(-1, restaurant.getId(), 2);
        Table table2 = new Table(-1, restaurant.getId(), 4);
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
