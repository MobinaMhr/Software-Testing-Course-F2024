package mizdooni.controllers;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.*;
import mizdooni.response.*;
import mizdooni.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewControllerTest {

    @Mock
    private ControllerUtils utils;
    @Mock
    private User user1;
    @Mock
    private User user2;
//    @Mock
    private Rating rating;
    @Mock
    private Restaurant restaurant;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private PagedList<Review> pagedReviews;
    private String comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rating = new Rating(){{food = 4; ambiance = 4.2; service = 3.8; overall= 4.1;}};

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(user1, rating, "This restaurant was perfect.", LocalDateTime.now()));
        reviews.add(new Review(user2, rating, "This restaurant was perfect.", LocalDateTime.now()));

        pagedReviews = new PagedList<>(reviews, 1, 5);
    }

    @Test
    void testGetReviewsFailToFindRestaurant() {
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(null);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reviewController.getReviews(restaurant.getId(), 1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test //TODO::Impossible Scenario
    void testFailingInGetReviews() throws RestaurantNotFound {
        when(restaurant.getId()).thenReturn(1);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        Exception exception = new RestaurantNotFound();
        when(reviewService.getReviews(restaurant.getId(), 1)).thenThrow(exception);
        ResponseException responseException = assertThrows(ResponseException.class,
                () -> reviewController.getReviews(restaurant.getId(), 1));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(exception.getMessage(), responseException.getMessage());
    }

//    @Test
//    void test() {
//        //
//    }
}