package mizdooni.controllers;
import mizdooni.exceptions.InvalidManagerRestaurant;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.exceptions.TableNotFound;
import mizdooni.exceptions.UserNotManager;
import mizdooni.model.*;
import mizdooni.response.ResponseException;
import mizdooni.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static mizdooni.controllers.ControllerUtils.DATE_FORMATTER;
import static mizdooni.service.ServiceUtils.*;
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

    @Test
    public void testFindAvailableTableFailToFindRestaurant(){
        String validDate = "2024-11-01";
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(null);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getReservations(restaurant.getId(), 1, validDate));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test
    public void testFindAvailableTableFailToFindReservation() throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        String validDate = "2024-11-01";
        LocalDate date = LocalDate.parse(validDate, DATE_FORMATTER);
        Exception ex = new UserNotManager();
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getReservations(restaurant.getId(), 1, date)).thenThrow(new UserNotManager());
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getReservations(restaurant.getId(), 1, validDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ex.getMessage(), exception.getMessage());
    }


}
