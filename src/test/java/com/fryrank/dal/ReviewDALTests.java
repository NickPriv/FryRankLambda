package com.fryrank.dal;

import com.fryrank.model.GetAllReviewsOutput;
import com.fryrank.model.Review;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static com.fryrank.TestConstants.TEST_RESTAURANT_ID;
import static com.fryrank.TestConstants.TEST_REVIEWS;
import static com.fryrank.dal.ReviewDALImpl.RESTAURANT_ID_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@RunWith(MockitoJUnitRunner.class)
public class ReviewDALTests {
    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    ReviewDALImpl reviewDAL;

    @Test
    public void testGetAllReviewsByRestaurantId_happyPath() throws Exception {
        final Query query = new Query();
        query.addCriteria(where(RESTAURANT_ID_KEY).is(TEST_RESTAURANT_ID));

        when(mongoTemplate.find(query, Review.class)).thenReturn(TEST_REVIEWS);

        final GetAllReviewsOutput expectedOutput = new GetAllReviewsOutput(TEST_REVIEWS);
        final GetAllReviewsOutput actualOutput = reviewDAL.getAllReviewsByRestaurantId(TEST_RESTAURANT_ID);
        assertEquals(actualOutput, expectedOutput);
    }

    @Test
    public void testGetAllReviewsByRestaurantId_noReviews() throws Exception {
        final Query query = new Query();
        query.addCriteria(where(RESTAURANT_ID_KEY).is(TEST_RESTAURANT_ID));

        final List<Review> expectedReviews = List.of();
        when(mongoTemplate.find(query, Review.class)).thenReturn(expectedReviews);

        final GetAllReviewsOutput expectedOutput = new GetAllReviewsOutput(expectedReviews);
        final GetAllReviewsOutput actualOutput = reviewDAL.getAllReviewsByRestaurantId(TEST_RESTAURANT_ID);
        assertEquals(actualOutput, expectedOutput);
    }

    @Test
    public void testGetAllReviewsByRestaurantId_nullRestaurantId() {
        assertThrows(NullPointerException.class, () -> reviewDAL.getAllReviewsByRestaurantId(null));
    }
}
