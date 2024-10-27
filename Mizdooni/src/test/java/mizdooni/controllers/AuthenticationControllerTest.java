package mizdooni.controllers;
import mizdooni.model.User;
import mizdooni.service.*;
import mizdooni.database.*;
import mizdooni.response.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {

    @Mock
    private User user1;
    @Mock
    private User user2;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUser_LoggedIn() {//TODO: should implement getters for response ?

        when(userService.getCurrentUser()).thenReturn(user1);

        Response response = authenticationController.user();
        assertEquals("current user", response.getMessage());
        assertEquals(user1, response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(true, response.isSuccess());
    }
}
