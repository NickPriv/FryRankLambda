package com.fryrank.domain;

import com.fryrank.dal.ReviewDAL;
import com.fryrank.model.GetAllReviewsOutput;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ReviewDomain {

    private ReviewDAL reviewDAL;

    public GetAllReviewsOutput getAllReviewsForRestaurant(@NonNull final String restaurantId) {
        return reviewDAL.getAllReviewsByRestaurantId(restaurantId);
    }
}
