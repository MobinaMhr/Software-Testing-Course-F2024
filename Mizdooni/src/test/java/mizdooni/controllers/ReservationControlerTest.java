package mizdooni.controllers;
import mizdooni.model.*;
import mizdooni.response.*;
import mizdooni.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationControlerTest {
    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}
