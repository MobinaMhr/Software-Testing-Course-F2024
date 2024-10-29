package mizdooni.controllers;
import mizdooni.model.*;
import mizdooni.response.ResponseException;
import mizdooni.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationControllerTest {
    @Mock
    private Restaurant restaurant;
    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private List<Table> tables;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tables = Arrays.asList(
                new Table(1, restaurant.getId(), 2),
                new Table(2, restaurant.getId(), 4),
                new Table(3, restaurant.getId(), 6)
        );
    }

    @Test
    public void testFindAvailableTableFailToParseLocalDate(){
        String invalidDate = "2024-13-01";
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getReservations(restaurant.getId(), 1, invalidDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_BAD_TYPE, exception.getMessage());
    }


}
