package mizdooni.controllers;
import mizdooni.database.Database;
import mizdooni.exceptions.*;
import mizdooni.model.*;
import mizdooni.response.*;
import mizdooni.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;

import static mizdooni.service.ServiceUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewControllerTest {

    @Mock
    private ControllerUtils utils;
    @Mock
    private User user1;
    @Mock
    private User user2;
    @Mock
    UserService userService;
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
    @DisplayName("Test Get Reviews Fail to Find Restaurant")
    void testGetReviewsFailToFindRestaurant() {
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(null);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reviewController.getReviews(restaurant.getId(), 1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test // Note that this was impossible scenario in code
    @DisplayName("Test Failing in Get Reviews")
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

    @Test
    @DisplayName("Test Get Reviews Successful Scenario")
    void testGetReviewsSuccessfulScenario() throws RestaurantNotFound {
        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("mew");
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reviewService.getReviews(restaurant.getId(), 1)).thenReturn(pagedReviews);
        Response response = reviewController.getReviews(restaurant.getId(), 1);
        String message = "reviews for restaurant (" + restaurant.getId() + "): " + restaurant.getName();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(pagedReviews, response.getData());
    }

    @Test
    @DisplayName("Test Add Review by Missed Params")
    void testAddReviewByMissedParams(){
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "it was awful");
        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("mew");
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException responseException = assertThrows(ResponseException.class,
                () -> reviewController.addReview(restaurant.getId(), params));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, responseException.getMessage());
    }

    @Test
    @DisplayName("Test Add Review Param Conversion Error")
    void testAddReviewParamConversionError(){
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "it was awful");
        params.put("rating", rating);
        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("mew");
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException responseException = assertThrows(ResponseException.class,
                () -> reviewController.addReview(restaurant.getId(), params));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ControllerUtils.PARAMS_BAD_TYPE, responseException.getMessage());
    }

    @Test
    @DisplayName("Test Add Review with Failure")
    void testAddReviewWithFailure()
            throws UserNotFound, ManagerCannotReview, UserHasNotReserved,
            RestaurantNotFound, InvalidReviewRating {
        Map<String, Object> params = new HashMap<>();
        String comment = "it was awful";
        params.put("comment", comment);

        Map<String, Number> ratingMap = Map.of(
                "food", 4.5,
                "service", 3,
                "ambiance", 5,
                "overall", 4
        );
        params.put("rating", ratingMap);

        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("mew");
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        Exception ex = new UserHasNotReserved();
        doThrow(ex).when(reviewService).addReview(anyInt(), any(), anyString());

        ResponseException responseException = assertThrows(ResponseException.class,
                () -> reviewController.addReview(restaurant.getId(), params)
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ex.getMessage(), responseException.getMessage());
    }

    @Test
    @DisplayName("Test Successful Add Review")
    void testSuccessfulAddReview(){
        Map<String, Object> params = new HashMap<>();
        String comment = "it was awful";
        params.put("comment", comment);

        Map<String, Number> ratingMap = Map.of(
                "food", 4.5,
                "service", 3,
                "ambiance", 5,
                "overall", 4
        );
        params.put("rating", ratingMap);

        when(restaurant.getId()).thenReturn(1);
        when(restaurant.getName()).thenReturn("mew");
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        Response response = reviewController.addReview(restaurant.getId(), params);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("review added successfully", response.getMessage());
    }

}


