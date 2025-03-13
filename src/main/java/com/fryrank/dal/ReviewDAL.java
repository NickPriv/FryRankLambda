package com.fryrank.dal;

import com.fryrank.model.GetAllReviewsOutput;

public interface ReviewDAL {

    GetAllReviewsOutput getAllReviewsByRestaurantId(final String restaurantId);
}
