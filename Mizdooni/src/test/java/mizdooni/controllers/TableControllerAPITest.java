package mizdooni.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.database.Database;
import mizdooni.exceptions.InvalidManagerRestaurant;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.exceptions.UserNotManager;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableController.class)
@DirtiesContext
public class TableControllerAPITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private TableService tableService;
    @MockBean
    private Database db;
    @MockBean
    private UserService userService;

    private Restaurant restaurant;
    private Table table;

    String url;

    private ResultActions performOnMvc(String url) throws Exception {
        return mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performOnMvc(String url, String body) throws Exception {
        return mockMvc.perform(request(HttpMethod.POST, url)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    private JsonNode getDataNode(ResultActions res) throws Exception {
        String body = res.andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("data");
    }

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

    @BeforeEach
    public void setup() throws Exception {
        url = "/tables";

        restaurant = getRestaurant();
        table = getTable();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(tableService.getTables(restaurant.getId())).thenReturn(List.of(table));
        when(restaurantService.getRestaurants(10, new RestaurantSearchFilter())).
                thenReturn(new PagedList<>(List.of(restaurant), 1, 15));
    }

    // --------------------------- Get Table --------------------------- //

    @Test
    @DisplayName("Getting result of getTable without the id parameter")
    public void testGetTables_MissingRestaurantIdParam_NotFound() throws Exception {
        performOnMvc(url).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Getting result of getTable with invalid id type")
    public void testGetTables_InvalidRestaurantIdType_BadRequest() throws Exception {
        url += "/Invalid_Restaurant_Id_Type";
        performOnMvc(url)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Getting result of getTable with non existing restaurant id")
    public void testGetTables_NonExistingRestaurant_NotFound() throws Exception {
        int restaurantId = 1500;
        url += "/" + restaurantId;
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(null);
        performOnMvc(url)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    @DisplayName("Getting result of getTable with successful scenario")
    public void testGetTables_ExistingRestaurant_Successful() throws Exception {
        url += "/" + restaurant.getId();

        performOnMvc(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].tableNumber").value(1))
                .andExpect(jsonPath("$.data[0].seatsNumber").value(4));
    }

    @Test
    public void testAddTable_MissingRestaurantIdParam_Successful() throws Exception {
        performOnMvc(url).andExpect(status().isNotFound());
    }

    // --------------------------- Add Table --------------------------- //

    @Test
    @DisplayName("Getting result of addTable with empty params")
    public void testAddTable_EmptyParams_ParamsMissing() throws Exception {
        url += "/" + restaurant.getId();
        Map<String, String> params = Map.of();

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(PARAMS_MISSING));
    }

    @Test
    @DisplayName("Getting result of addTable with invalid id type")
    public void testAddTables_InvalidRestaurantIdType_BadRequest() throws Exception {
        url += "/Invalid_Restaurant_Id_Type";
        Map<String, String> params = Map.of(
                "seatsNumber", "1"
        );

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Getting result of addTable with non existing restaurant id")
    public void testAddTables_NonExistingRestaurant_NotFound() throws Exception {
        int restaurantId = 1500;
        url += "/" + restaurantId;
        Map<String, String> params = Map.of(
                "seatsNumber", "1"
        );

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    @DisplayName("Getting result of addTable with wrong param type")
    public void testAddTables_wrongParamType_BadRequest() throws Exception {
        url += "/" + restaurant.getId();
        Map<String, String> params = Map.of(
                "seatsNumber", "first"
        );

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(PARAMS_BAD_TYPE));
    }
    
    @Test
    @DisplayName("Getting result of addTable with user who is not manager")
    public void testAddTables_NoLoggedInUser_UserNotManager() throws Exception {
        url += "/" + restaurant.getId();
        Map<String, String> params = Map.of(
                "seatsNumber", "1"
        );

        Exception ex = new UserNotManager();
        doThrow(ex).when(tableService).addTable(eq(restaurant.getId()), eq(Integer.parseInt(params.get("seatsNumber"))));

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("UserNotManager"))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }

    @Test
    @DisplayName("Getting result of addTable with invalid manager")
    public void testAddTables_InvalidManager_InvalidManagerRestaurant() throws Exception {
        url += "/" + restaurant.getId();
        Map<String, String> params = Map.of(
                "seatsNumber", "1"
        );

        Exception ex = new InvalidManagerRestaurant();
        doThrow(ex).when(tableService).addTable(eq(restaurant.getId()), eq(Integer.parseInt(params.get("seatsNumber"))));

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("InvalidManagerRestaurant"))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }

    @Test
    @DisplayName("Getting result of addTable with successful scenario")
    public void testAddTable_CorrectParams_Successful() throws Exception {
        url += "/" + restaurant.getId();
        Map<String, String> params = Map.of(
                "seatsNumber", "1"
        );

        performOnMvc(url, mapper.writeValueAsString(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("table added"));
    }

    @Test
    public void testAddTables_InvalidRestaurantIdType_BadRequestResponse() throws Exception {
        url += "/Invalid_Restaurant_Id_Type";
        performOnMvc(url).andExpect(status().isBadRequest());
    }
}
