package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingTest {
    private Rating rating;

    @BeforeEach
    public void setUp(){
        rating = new Rating();
        rating.service = 4.2;
        rating.food = 4.6;
        rating.ambiance = 3.4;
        rating.overall = 4.659;
    }

    @Test
    @DisplayName("test getStarCount method")
    public void testGetStarCount(){
        assertEquals(5, rating.getStarCount());
    }
}
