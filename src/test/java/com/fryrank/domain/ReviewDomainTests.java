package com.fryrank.domain;

import com.fryrank.dal.ReviewDAL;
import com.fryrank.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.fryrank.TestConstants.TEST_RESTAURANT_ID;
import static com.fryrank.TestConstants.TEST_REVIEWS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ReviewDomainTests {
    @Mock
    ReviewDAL reviewDAL;

    @InjectMocks
    ReviewDomain domain;

    @Test
    public void testGetAllReviewsForRestaurant() {
        final GetAllReviewsOutput expectedOutput = new GetAllReviewsOutput(TEST_REVIEWS);
        when(reviewDAL.getAllReviewsByRestaurantId(TEST_RESTAURANT_ID)).thenReturn(expectedOutput);

        final GetAllReviewsOutput actualOutput = domain.getAllReviewsForRestaurant(TEST_RESTAURANT_ID);
        assertEquals(expectedOutput, actualOutput);
    }
}
