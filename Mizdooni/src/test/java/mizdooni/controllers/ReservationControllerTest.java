package mizdooni.controllers;
import mizdooni.exceptions.*;
import mizdooni.model.*;
import mizdooni.response.*;
import mizdooni.response.ResponseException;
import mizdooni.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static mizdooni.controllers.ControllerUtils.DATE_FORMATTER;
import static mizdooni.service.ServiceUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationControllerTest {
    @Mock
    private Reservation reservation;
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

    @Test
    public void testFindAvailableTableSuccessfully() throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        String validDate = "2024-11-01";
        LocalDate date = LocalDate.parse(validDate, DATE_FORMATTER);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getReservations(restaurant.getId(), 1, date)).thenReturn(reservations);
        Response response = reservationController.getReservations(restaurant.getId(), 1, validDate);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("restaurant table reservations", response.getMessage());
        assertEquals(reservations, response.getData());
        assertEquals(true, response.isSuccess());
    }

    @Test
    public void testGetCustomerReservationsSuccessfully() throws UserNotFound, UserNoAccess {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(reservationService.getCustomerReservations(1)).thenReturn(reservations);
        Response response = reservationController.getCustomerReservations(1);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("user reservations", response.getMessage());
        assertEquals(true, response.isSuccess());
        assertEquals(reservations, response.getData());
    }

    @Test
    public void testGetCustomerReservationsFailure() throws UserNotFound, UserNoAccess {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        Exception ex = new UserNotFound();

        when(reservationService.getCustomerReservations(1)).thenThrow(ex);
        ResponseException responseException = assertThrows(ResponseException.class,
                () -> reservationController.getCustomerReservations(1));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ex.getMessage(), responseException.getMessage());

    }

    @Test
    public void testFindAvailableTimesFailToParseLocalDate(){
        String invalidDate = "2024-13-01";
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getAvailableTimes(restaurant.getId(), 1, invalidDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testFindAvailableTimesFailToFindRestaurant(){
        String validDate = "2024-11-01";
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(null);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getAvailableTimes(restaurant.getId(), 1, validDate));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test
    void testGetAvailableTimesFailToFindItems() throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        Exception ex = new DateTimeInThePast();
        String validDate = "2024-11-01";
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getAvailableTimes(eq(restaurant.getId()), eq(1), any(LocalDate.class))).thenThrow(ex);

        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getAvailableTimes(restaurant.getId(), 1, validDate));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ex.getMessage(), exception.getMessage());
    }

    @Test
    void testGetAvailableTimesSuccessful() throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        List<LocalTime> responseTimes = new ArrayList<>(List.of(
                LocalTime.of(10, 30),
                LocalTime.of(14, 45),
                LocalTime.of(18, 0)
        ));
        String validDate = "2024-11-01";
//        List<Table> emptyTables = new ArrayList<>();
//        when(restaurant.getTables()).thenReturn(emptyTables);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getAvailableTimes(eq(restaurant.getId()), eq(1),
                any(LocalDate.class))).thenReturn(responseTimes);

        Response response = reservationController.getAvailableTimes(restaurant.getId(), 1, validDate);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("available times", response.getMessage());
        assertEquals(responseTimes, response.getData());
    }

    @Test
    void testCancelReservationFailure() throws ReservationCannotBeCancelled, UserNotFound, ReservationNotFound {
        Exception ex = new ReservationCannotBeCancelled();
        doThrow(ex).when(reservationService).cancelReservation(eq(1));

        ResponseException responseException = assertThrows(ResponseException.class, () ->
                reservationController.cancelReservation(1));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(ex.getMessage(), responseException.getMessage());
    }

}
