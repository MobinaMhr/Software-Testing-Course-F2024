package mizdooni.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.exceptions.InvalidManagerRestaurant;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.*;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TableController.class)
public class RestaurantControllerAPITest {
    String url;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;
    @MockBean
    private TableService tableService;
    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private Restaurant restaurant;

    @MockBean
    private ControllerUtils controllerUtils;

    private Restaurant getRestaurant() throws Exception {
        Address address = new Address("Iran", "Tehran", "Kargar");
        User manager = new User("Akbar Akbari", "password", "AkbarAkbari@example.com", address, User.Role.manager);
        String restaurantName = "Baradaran Akbari bejoz Davood";
        String restaurantType = "Kababi";
        return new Restaurant(restaurantName, manager, restaurantType, LocalTime.now(), LocalTime.now().plusHours(8),
                "100% goosfandi", address, ":|");
    }
    private Table getTable() throws Exception {
        return new Table(1, restaurant.getId(), 4);
    }
    private ResultActions performOnMvc(String url) throws Exception {
        return mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
    }

    @BeforeEach
    public void setup() throws Exception {
        url = "/restaurants";

        restaurant = getRestaurant();

        // Mocking the service response for the /restaurants endpoint
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(restaurantService.getRestaurants(10, new RestaurantSearchFilter())).
                thenReturn(new PagedList<>(List.of(restaurant), 1, 15));
    }

    // --------------------------- Get Restaurant --------------------------- //

    @Test
    @DisplayName("")
    public void testGetRestaurant_NonExistingRestaurant_NotFound() throws Exception {
        int nonExistentRestaurantId = 1500;
        url += "/" + nonExistentRestaurantId;

        when(restaurantService.getRestaurant(nonExistentRestaurantId)).thenReturn(null);

        performOnMvc(url)
                .andExpect(status().isNotFound());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    @DisplayName("")
    public void testGetRestaurant_CorrectRestaurantId_() throws Exception {
        url += "/" + restaurant.getId();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        performOnMvc(url)
                .andExpect(status().isOk());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    public void testGetRestaurant_WrongRestaurantIdType_NotFound() throws Exception {
        String restaurantId = "wrong_ID";
        url += "/" + restaurantId;
        performOnMvc(url)
                .andExpect(status().isNotFound());
    }

//    @GetMapping("/restaurants")
//    public Response getRestaurants(@RequestParam int page, RestaurantSearchFilter filter) {
//        try {
//            PagedList<Restaurant> restaurants = restaurantService.getRestaurants(page, filter);
//            return Response.ok("restaurants listed", restaurants);
//        } catch (Exception ex) {
//            throw new ResponseException(HttpStatus.BAD_REQUEST, ex);
//        }
//    }

    // --------------------------- Get Restaurants --------------------------- //

    private Stream<Arguments> restaurantProvider() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(1),
                Arguments.of(2)
        );
    }
    /*@ParameterizedTest
    @MethodSource("restaurantProvider")
    public void testGetRestaurants_NoRestaurantsMatching_(int restaurantCount) throws Exception {
        PagedList<Restaurant> restaurants;
        for (int i = 0; i < restaurantCount; i++) {
            restaurants.
            restaurants.add(getRestaurant());
        }
        Exception ex = new ResponseException();
    }*/

    /*@Test
    @DisplayName("")
    public void testGetRestaurants_OneRestaurantsMatching_() throws Exception {
        Exception ex = new ResponseException();
    }

    @Test
    @DisplayName("")
    public void testGetRestaurants_ManyRestaurantsMatching_() throws Exception {
        Exception ex = new ResponseException();
    }*/

    @Test
    @DisplayName("Performing the GET request with an invalid page number")
    public void testGetRestaurants_InvalidPage() throws Exception {
        mockMvc.perform(get(url)
                .param("page", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @GetMapping("/restaurants")
    public Response getRestaurants(@RequestParam int page, RestaurantSearchFilter filter) {
        try {
            PagedList<Restaurant> restaurants = restaurantService.getRestaurants(page, filter);
            return Response.ok("restaurants listed", restaurants);
        } catch (Exception ex) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("Performing the GET request without the page parameter")
    public void testGetRestaurants_MissingPageParam() throws Exception {
        performOnMvc(url).andExpect(status().isBadRequest());
    }

// --------------------------- Get Manager Restaurants --------------------------- //
    @Test
    @DisplayName("throwing exeptions when db is null")
    public void testGetManagerRestaurants_CatchDbNullPointerExeption(){
        fail("empty");
    }

    @Test
    @DisplayName("success scenario")
    public void testGetManagerRestaurants_Success(){
        fail("empty");
    }
// --------------------------- Add Restaurant --------------------------- //
    @Test
    @DisplayName("missing params")
    public void testAddRestaurant_MissingParams(){
        fail("empty");
    }
    @Test
    @DisplayName("bad params:localTime bad format")
    public void testAddRestaurant_BadParamsLocalTimeBadFormat(){
        fail("empty");
    }
    @Test
    @DisplayName("bad params:name isn't a String")
    public void testAddRestaurant_BadParamsName(){
        fail("empty");
    }
    @Test
    @DisplayName("bad params:type isn't a String")
    public void testAddRestaurant_BadParamsType(){
        fail("empty");
    }
    @Test
    @DisplayName("bad params:description isn't a String")
    public void testAddRestaurant_BadParamsDescription(){
        fail("empty");
    }
    @Test
    @DisplayName("bad params:image isn't a String")
    public void testAddRestaurant_BadParamsImage(){
        fail("empty");
    }
    @Test
    @DisplayName("bad params:addr isn't a String")
    public void testAddRestaurant_BadParamsAddr(){
        fail("empty");
    }
    @Test
    @DisplayName("param doesn't exist in controlerUtils")
    public void testAddRestaurant_NotExistingParam(){
        fail("empty");
    }
    @Test
    @DisplayName("add duplicated restaurant")
    public void testAddRestaurant_DuplicateRestaurantName(){
        fail("empty");
    }
    @Test
    @DisplayName("add restaurant by no logged in user or not manager user")
    public void testAddRestaurant_BadLoggedInUser(){
        fail("empty");
    }
    @Test
    @DisplayName("add restaurant by invalid working time")
    public void testAddRestaurant_ByInvalidWorkingTime(){
        fail("empty");
    }
    @Test
    @DisplayName("success scenario")
    public void testAddRestaurant_Successfull(){
        fail("empty");
    }
// --------------------------- Validate Restaurant Name --------------------------- //
    @Test
    @DisplayName("validate existing restaurant name")
    public void testValidateRestaurantName_ExistingRestaurantName(){
        fail("empty");
    }
    @Test
    @DisplayName("validate unique restaurant name")
    public void testValidateRestaurantName_UniqueRestaurantName(){
        fail("empty");
    }
// --------------------------- Get Restaurant Types --------------------------- //
// --------------------------- Get Restaurant Locations --------------------------- //

}
