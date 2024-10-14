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

    @Test
    public void testAddMultipleTables(){
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
    public void testGetTableFromNoTables(){//TODO: you should change the naming!
        assertNull(restaurant.getTable(1));
    }

    @Test
    public void testGetTableWhichDosntExist(){//TODO: should change the naming!
        Table table1 = new Table(-1, restaurant.getId(), 2);
        restaurant.addTable(table1);
        assertNull(restaurant.getTable(5));
    }

    @Test
    public void testGetTableForExistingTable(){
        Table table1 = new Table(-1, restaurant.getId(), 2);
        restaurant.addTable(table1);
        assertEquals(table1, restaurant.getTable(1));
    }

    @Test
    public void testGetMaxSitsNumber(){
        Table table1 = new Table(-1, restaurant.getId(), 2);
        Table table2 = new Table(-1, restaurant.getId(), 4);
        restaurant.addTable(table1);
        restaurant.addTable(table2);
        assertEquals(4, restaurant.getMaxSeatsNumber());
    }
}
