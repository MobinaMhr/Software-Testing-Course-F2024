package mizdooni.model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private Address address;
    private User user;

    @BeforeEach
    public void setUp() {
        address = new Address("Iran", "Tehran", "Kargar");
        user = new User("testUser", "testPassword", "test@example.com", address, User.Role.client);
    }

    @Test
    public void testCheckCorrectPassword(){
        assertTrue(user.checkPassword("testPassword"));
    }
}
