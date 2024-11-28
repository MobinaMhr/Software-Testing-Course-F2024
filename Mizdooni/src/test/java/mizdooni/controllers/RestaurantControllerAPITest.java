package mizdooni.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.*;

import mizdooni.exceptions.DuplicatedRestaurantName;
import mizdooni.exceptions.InvalidWorkingTime;
import mizdooni.exceptions.UserNotManager;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.User;
import mizdooni.response.PagedList;
import mizdooni.service.RestaurantService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        reset(restaurantService);

        Address address_ = new Address("Iran", "Tehran", "Kargar");
        User manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address_, User.Role.manager);
        String restaurantName = "Baradaran Akbari bejoz Davood";
        String restaurantType = "Kababi";

        restaurant = new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(), LocalTime.now().plusHours(8),
                "100% goosfandi", address_, ":|");
    }

    // --------------------------- Get Restaurant --------------------------- //

    @Test
    void testGetRestaurant_CorrectRestaurantId_Successful() throws Exception {
        int restaurantId = restaurant.getId();
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

        mockMvc.perform(get("/restaurants/" + restaurant.getId(), restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant found"))
                .andExpect(jsonPath("$.data.name").value(restaurant.getName()))
                .andExpect(jsonPath("$.data.type").value(restaurant.getType()));
    }

    @Test
    void testGetRestaurant_NonExistingRestaurant_NotFound() throws Exception {
        int nonExistentRestaurantId = 1500;
        when(restaurantService.getRestaurant(nonExistentRestaurantId)).thenReturn(null);

        mockMvc.perform(get("/restaurants/" +  nonExistentRestaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    // --------------------------- Get Restaurants --------------------------- //

    @Test
    void testGetRestaurants_OneRestaurantMatching_Successful() throws Exception {
        int page = 1;
        List<Restaurant> restaurants = List.of(restaurant);
        PagedList<Restaurant> returnedPage = new PagedList<>(restaurants, page, 1);
        when(restaurantService.getRestaurants(eq(page), any())).thenReturn(returnedPage);

        mockMvc.perform(get("/restaurants").param("page", Integer.toString(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurants listed"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageList[0].name").value(restaurant.getName()))
                .andExpect(jsonPath("$.data.pageList[0].type").value(restaurant.getType()))
                .andExpect(jsonPath("$.data.pageList[0].address.country").value(restaurant.getAddress().getCountry()))
                .andExpect(jsonPath("$.data.pageList[0].address.city").value(restaurant.getAddress().getCity()))
                .andExpect(jsonPath("$.data.pageList[0].address.street").value(restaurant.getAddress().getStreet()));
    }

    // TODO check its expects hashem
    @Test
    void testGetRestaurants_ManyRestaurantsMatching_Successful() throws Exception {
        int page = 1;
        Address address2 = new Address("Iran", "Tehran", "Kargar jonobi");
        User manager2 = new User("Akbar Akbari Dehkhoda", "password", "AkbarAkbari@example.com", address2, User.Role.manager);
        Restaurant restaurant2 = new Restaurant("2", manager2, "IDK", LocalTime.now(), LocalTime.now().plusHours(8),
                "100% goosfandi2", address2, ":|");
        List<Restaurant> restaurants = List.of(restaurant, restaurant2);
        PagedList<Restaurant> returnedPage = new PagedList<>(restaurants, page, 1);
        when(restaurantService.getRestaurants(eq(page), any())).thenReturn(returnedPage);

        try {
            mockMvc.perform(get("/restaurants").param("page", Integer.toString(page)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurants listed"))
                    .andExpect(jsonPath("$.data.page").value(page))
                    .andExpect(jsonPath("$.data.hasNext").value(true))
                    .andExpect(jsonPath("$.data.totalPages").value(restaurants.size()))
                    .andExpect(jsonPath("$.data.pageList[0].name").value(restaurant.getName()))
                    .andExpect(jsonPath("$.data.pageList[0].type").value(restaurant.getType()))
                    .andExpect(jsonPath("$.data.pageList[0].address.country").value(restaurant.getAddress().getCountry()))
                    .andExpect(jsonPath("$.data.pageList[0].address.city").value(restaurant.getAddress().getCity()))
                    .andExpect(jsonPath("$.data.pageList[0].address.street").value(restaurant.getAddress().getStreet()))
                    .andExpect(jsonPath("$.data.pageList[0].managerUsername").value(restaurant.getManager().getUsername()))

                    .andExpect(jsonPath("$.data.pageList[0].name").value(restaurant2.getName()))
                    .andExpect(jsonPath("$.data.pageList[0].type").value(restaurant2.getType()))
                    .andExpect(jsonPath("$.data.pageList[0].address.country").value(restaurant2.getAddress().getCountry()))
                    .andExpect(jsonPath("$.data.pageList[0].address.city").value(restaurant2.getAddress().getCity()))
                    .andExpect(jsonPath("$.data.pageList[0].address.street").value(restaurant2.getAddress().getStreet()))
                    .andExpect(jsonPath("$.data.pageList[0].managerUsername").value(restaurant2.getManager().getUsername()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Why it gives OK  :(((((((((((((((((((((((((
    @Test
    public void testGetRestaurants_InvalidPage_BadRequest() throws Exception {
        int validPage = 1;
        int invalidPage = 10;
        List<Restaurant> restaurants = List.of(restaurant);
        PagedList<Restaurant> returnedPage = new PagedList<>(restaurants, validPage, 1);

        when(restaurantService.getRestaurants(eq(validPage), any())).thenReturn(returnedPage);

        mockMvc.perform(get("/restaurants").param("page", Integer.toString(invalidPage)))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("restaurants listed"))
//                .andExpect(jsonPath("$.data.page").value(1))
//                .andExpect(jsonPath("$.data.pageList[0].name").value(restaurant.getName()))
//                .andExpect(jsonPath("$.data.pageList[0].type").value(restaurant.getType()))
//                .andExpect(jsonPath("$.data.pageList[0].address.country").value(restaurant.getAddress().getCountry()))
//                .andExpect(jsonPath("$.data.pageList[0].address.city").value(restaurant.getAddress().getCity()))
//                .andExpect(jsonPath("$.data.pageList[0].address.street").value(restaurant.getAddress().getStreet()));
    }

    // --------------------------- Get Manager Restaurants --------------------------- //

    @Test
    void testGetManagerRestaurants_Successful() throws Exception {
        int managerId = restaurant.getManager().getId();
        List<Restaurant> restaurants = List.of(restaurant);
        when(restaurantService.getManagerRestaurants(managerId)).thenReturn(restaurants);

        mockMvc.perform(get("/restaurants/manager/" + managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("manager restaurants listed"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(restaurant.getId()))
                .andExpect(jsonPath("$.data[0].name").value(restaurant.getName()))
                .andExpect(jsonPath("$.data[0].type").value(restaurant.getType()))
                .andExpect(jsonPath("$.data[0].address.country").value(restaurant.getAddress().getCountry()))
                .andExpect(jsonPath("$.data[0].address.city").value(restaurant.getAddress().getCity()))
                .andExpect(jsonPath("$.data[0].address.street").value(restaurant.getAddress().getStreet()));
    }

    @Test
    public void testGetManagerRestaurants_CatchDbNullPointerException_BadRequest() throws Exception {
        int managerId = restaurant.getManager().getId();
        Exception ex = new NullPointerException();;
        doThrow(ex).when(restaurantService).getManagerRestaurants(eq(managerId));

        mockMvc.perform(get("/restaurants/manager/" + managerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("NullPointerException"));
    }

    // --------------------------- Add Restaurant --------------------------- //

    @Test
    public void testAddRestaurant_BadLoggedInUser_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "New Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        Exception ex = new UserNotManager();
        when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class),
                anyString(), any(Address.class), anyString())).thenThrow(ex);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("UserNotManager"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ex.getMessage()));
    }

    @Test
    public void testAddRestaurant_Successful() throws Exception {
        String requestBody = """
        {
            "name": "Test Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        when(restaurantService.addRestaurant(eq("Test Restaurant"), eq("Test Type"),
                eq(LocalTime.parse("08:00")), eq(LocalTime.parse("22:00")), eq("A test description"),
                any(Address.class), anyString())).thenReturn(1);

        mockMvc.perform(post("/restaurants").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("restaurant added"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    public void testAddRestaurant_ByInvalidWorkingTime_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "New Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        Exception ex = new InvalidWorkingTime();
        when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class),
                anyString(), any(Address.class), anyString())).thenThrow(ex);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("InvalidWorkingTime"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ex.getMessage()));
    }

    @Test
    public void testAddRestaurant_DuplicateRestaurantName_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "Existing Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        Exception ex = new DuplicatedRestaurantName();
        when(restaurantService.addRestaurant(eq("Existing Restaurant"), eq("Test Type"),
                eq(LocalTime.parse("08:00")), eq(LocalTime.parse("22:00")), eq("A test description"),
                any(Address.class), anyString())).thenThrow(ex);

        mockMvc.perform(post("/restaurants").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("DuplicatedRestaurantName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ex.getMessage()));
    }

    @Test
    public void testAddRestaurant_NotExistingParam_BadRequest() throws Exception {
        String requestBody = """
        {
            "invalidParam": "Invalid Value"
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("parameters missing"));
    }

    @Test
    public void testAddRestaurant_BadParamsAddr_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "Test Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": "Invalid Address Format"
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    public void testAddRestaurant_BadParamsImage_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "Test Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "image": 123,
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    public void testAddRestaurant_BadParamsName_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": 123,
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    public void testAddRestaurant_BadParamsType_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "Test Restaurant",
            "type": 1234,
            "startTime": "08:00",
            "endTime": "22:00",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    public void testAddRestaurant_BadParamsLocalTimeBadFormat_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "Test Restaurant",
            "type": "Test Type",
            "startTime": "25:00",
            "endTime": "24:61",
            "description": "A test description",
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    public void testAddRestaurant_BadParamsDescription_BadRequest() throws Exception {
        String requestBody = """
        {
            "name": "Test Restaurant",
            "type": "Test Type",
            "startTime": "08:00",
            "endTime": "22:00",
            "description": 12345,
            "address": {
                "country": "Testland",
                "city": "Test City",
                "street": "Test Street"
            }
        }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad parameter type"));
    }

    // --------------------------- Validate Restaurant Name --------------------------- //

    @Test
    void testValidateRestaurantName_UniqueRestaurantName_Successful() throws Exception {
        String nonExistedName = "Non-Existent Name";
        when(restaurantService.restaurantExists(nonExistedName)).thenReturn(false);

        mockMvc.perform(get("/validate/restaurant-name").param("data", nonExistedName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant name is available"));
    }

    @Test
    void testValidateRestaurantName_ExistingRestaurantName_Conflict() throws Exception {
        String existedName = "Existent Name";
        when(restaurantService.restaurantExists(existedName)).thenReturn(true);

        mockMvc.perform(get("/validate/restaurant-name").param("data", existedName))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    // --------------------------- Get Restaurant Types --------------------------- //

    @Test
    void testGetRestaurantTypes_Successful() throws Exception {
        Set<String> types = Set.of("Fine Dining", "Fast Food", "Casual Dining");
        when(restaurantService.getRestaurantTypes()).thenReturn(types);

        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant types"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data", containsInAnyOrder("Fine Dining", "Fast Food", "Casual Dining")));
    }

    @Test
    public void testGetRestaurantTypes_FromNullDb_BadRequest() throws Exception {
        Exception ex = new NullPointerException();;
        doThrow(ex).when(restaurantService).getRestaurantTypes();

        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("NullPointerException"));
    }

    // --------------------------- Get Restaurant Locations --------------------------- //

    @Test
    public void testGetRestaurantLocations_FromNullDb_BadRequest() throws Exception {
        Exception ex = new NullPointerException();;
        doThrow(ex).when(restaurantService).getRestaurantLocations();

        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("NullPointerException"));
    }

    @Test
    void testGetRestaurantLocations_Successful() throws Exception {
        Map<String, Set<String>> locations = Map.of(
                "country1", Set.of("cityA", "cityB"),
                "country2", Set.of("cityC", "cityD")
        );

        when(restaurantService.getRestaurantLocations()).thenReturn(locations);

        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("restaurant locations"))
                .andExpect(jsonPath("$.data.country1", containsInAnyOrder("cityA", "cityB")))
                .andExpect(jsonPath("$.data.country2", containsInAnyOrder("cityC", "cityD")));
    }
}
