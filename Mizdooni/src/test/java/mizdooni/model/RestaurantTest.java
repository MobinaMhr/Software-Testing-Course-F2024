package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestaurantTest {
    private Restaurant restaurant;
    private User manager;
    Table table1;
    Table table2;
    private String restaurantName;
    private String restaurantType;
    Address address;


    User user1;
    User user2;

    ArrayList<Table> tables = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        address = new Address("Iran", "Tehran", "Kargar");
        manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address,
                User.Role.manager);

        restaurantName = "Baradaran Akbari bejoz Davood";
        restaurantType = "Kababi";

        restaurant = new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(),
                LocalTime.now().plusHours(8), "100% goosfandi", address, ":|");
        table1 = new Table(-1, restaurant.getId(), 2);
        table2 = new Table(2, restaurant.getId(), 4);

        tables.add(table1);
        tables.add(table2);

        user1 = new User("user1", "user1Password", "user1@example.com", address, User.Role.client);
        user2 = new User("user2", "user2Password", "user2@example.com", address, User.Role.client);
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

    @ParameterizedTest
    @MethodSource("tableProvider")
    @DisplayName("Test Getting Table from Restaurant\n Getting Table from Empty Restaurant \n Getting non Existing Table \n Getting Table for Existing Table")
    public void testGettingTableFromRestaurant(int tableCount, int expectedTableIndex) {
        for (int i = 0; i < tableCount; i++) {
            restaurant.addTable(tables.get(i));
        }

        Table expectedTable = (expectedTableIndex) < 0 ? null : tables.get(expectedTableIndex);

        assertEquals(tableCount, restaurant.getTables().size());
        assertEquals(expectedTable, restaurant.getTable(2));
    }
    private Stream<Arguments> tableProvider() {
        return Stream.of(
                Arguments.of(0, -1),
                Arguments.of(1, -1),
                Arguments.of(2, 1)
        );
    }

    @Test
    @DisplayName("Test Getting Max Seats Number")
    public void testGettingMaxSeatsNumber(){
        restaurant.addTable(table1);
        restaurant.addTable(table2);
        assertEquals(4, restaurant.getMaxSeatsNumber());
    }

    @Test
    @DisplayName("Test Getting Max Seats Number for Empty Restaurant")
    public void testGettingMaxSeatsNumberForEmptyRestaurant(){
        Assertions.assertEquals(0, restaurant.getMaxSeatsNumber());
    }

    @Test
    @DisplayName("Test Adding Single Review")
    public void testAddingSingleReview(){
        Rating rating = new Rating(){{food = 4; ambiance = 4.2; service = 3.8; overall= 4.1;}};
        Review review1 = new Review(user1, rating, "This restaurant was perfect.", LocalDateTime.now());

        restaurant.addReview(review1);

        assertEquals(1, restaurant.getReviews().size());
        assertEquals(review1, restaurant.getReviews().getFirst());
    }

    @Test
    @DisplayName("Test Adding Multiple Reviews")
    public void testAddingMultipleReviews() {
        Rating rating1 = new Rating(){{food = 4; ambiance = 4.2; service = 3.8; overall= 4.1;}};
        Rating rating2 = new Rating(){{food = 4; ambiance = 4.2; service = 4.1; overall= 4.1;}};
        Review review1 = new Review(user1, rating1, "This restaurant was perfect.", LocalDateTime.now());
        Review review2 = new Review(user2, rating2, "This restaurant was not perfect.", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        assertEquals(2, restaurant.getReviews().size());
        assertEquals(review1, restaurant.getReviews().getFirst());
        assertEquals(review2, restaurant.getReviews().get(1));
    }

    @Test
    @DisplayName("checks the updating user review")
    public void testChangingUserReview(){
        Rating rating1 = new Rating(){{food = 4; ambiance = 4.2; service = 3.8; overall= 4.1;}};
        Rating rating2 = new Rating(){{food = 4; ambiance = 4.2; service = 4.1; overall= 4.1;}};
        Review review1 = new Review(user1, rating1, "This restaurant was perfect.", LocalDateTime.now());
        Review review2 = new Review(user1, rating2, "This restaurant was not perfect.", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        assertEquals(1, restaurant.getReviews().size());
        assertEquals(review2, restaurant.getReviews().getFirst());

    }

}