package com.fryrank.dal;

import com.fryrank.model.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fryrank.Constants.ISO_DATE_TIME;
import static com.fryrank.Constants.ACCOUNT_ID_KEY;
import static com.fryrank.Constants.PRIMARY_KEY;
import static com.fryrank.Constants.REVIEW_COLLECTION_NAME;
import static com.fryrank.Constants.USER_METADATA_COLLECTION_NAME;
import static com.fryrank.Constants.USER_METADATA_OUTPUT_FIELD_NAME;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class ReviewDALImpl implements ReviewDAL {

    public static final String RESTAURANT_ID_KEY = "restaurantId";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public GetAllReviewsOutput getAllReviewsByRestaurantId(@NonNull final String restaurantId) {
        final Criteria equalToRestaurantIdCriteria = Criteria.where(RESTAURANT_ID_KEY).is(restaurantId);
        final MatchOperation filterToRestaurantId = match(equalToRestaurantIdCriteria);

        LookupOperation lookup = LookupOperation.newLookup()
                .from(USER_METADATA_COLLECTION_NAME)
                .localField(ACCOUNT_ID_KEY)
                .foreignField(PRIMARY_KEY)
                .as(USER_METADATA_OUTPUT_FIELD_NAME);
        AggregationOperation unwind = Aggregation.unwind("userMetadata");
        final Aggregation aggregation = newAggregation(filterToRestaurantId, lookup, unwind);
        final AggregationResults<Review> result = mongoTemplate.aggregate(aggregation, REVIEW_COLLECTION_NAME, Review.class);

        return new GetAllReviewsOutput(result.getMappedResults());
    }

    @Override
    public GetAllReviewsOutput getAllReviewsByAccountId(@NonNull final String accountId) {
        final Criteria equalToAccountIdCriteria = Criteria.where(ACCOUNT_ID_KEY).is(accountId);
        final MatchOperation filterToAccountId = match(equalToAccountIdCriteria);

        LookupOperation lookup = LookupOperation.newLookup()
                .from(USER_METADATA_COLLECTION_NAME)
                .localField(ACCOUNT_ID_KEY)
                .foreignField(PRIMARY_KEY)
                .as(USER_METADATA_OUTPUT_FIELD_NAME);
        AggregationOperation unwind = Aggregation.unwind("userMetadata");
        final Aggregation aggregation = newAggregation(filterToAccountId, lookup, unwind);
        final AggregationResults<Review> result = mongoTemplate.aggregate(aggregation, REVIEW_COLLECTION_NAME, Review.class);

        return new GetAllReviewsOutput(result.getMappedResults());
    }

    @Override
    public GetAllReviewsOutput getTopMostRecentReviews(@NonNull final Integer count){
        final Query query= new Query();
        query.with(Sort.by(Sort.Direction.DESC, ISO_DATE_TIME));
        query.limit(count);
        final List<Review> reviews = mongoTemplate.find(query, Review.class);

        return new GetAllReviewsOutput(reviews);
    }

    @Override
    public GetAggregateReviewInformationOutput getAggregateReviewInformationForRestaurants(@NonNull final List<String> restaurantIds, @NonNull final AggregateReviewFilter aggregateReviewFilter) {
        final Query query = new Query().addCriteria(Criteria.where(RESTAURANT_ID_KEY).in(restaurantIds));

        Map<String, AggregateReviewInformation> restaurantIdToAggregateReviewInformation = new HashMap<String, AggregateReviewInformation>();

        final Criteria includeRestaurantIdsCriteria = Criteria.where(RESTAURANT_ID_KEY).in(restaurantIds);
        final MatchOperation filterToRestaurantId = match(includeRestaurantIdsCriteria);
        final GroupOperation averageScoreGroupOperation = group(RESTAURANT_ID_KEY).avg("score").as("avgScore");
        final Aggregation aggregation = newAggregation(filterToRestaurantId, averageScoreGroupOperation);
        final AggregationResults<AggregateReviewInformation> result = mongoTemplate.aggregate(aggregation, REVIEW_COLLECTION_NAME, AggregateReviewInformation.class);
        final List<AggregateReviewInformation> aggregateReviewInformationList = result.getMappedResults();

        aggregateReviewInformationList.stream().forEach(
                aggregateReviewInformation -> {
                    final Float averageScore = aggregateReviewFilter.getIncludeRating()
                            ? BigDecimal.valueOf(aggregateReviewInformation.getAvgScore()).setScale(1, RoundingMode.DOWN).floatValue()
                            : null;

                    restaurantIdToAggregateReviewInformation.put(aggregateReviewInformation.getRestaurantId(), new AggregateReviewInformation(aggregateReviewInformation.getRestaurantId(), averageScore));
                }
        );

        return new GetAggregateReviewInformationOutput(restaurantIdToAggregateReviewInformation);
    }

    @Override
    public Review addNewReview(@NonNull final Review review) {
        final Query query = new Query().addCriteria(Criteria.where("_id").is(review.getRestaurantId() + ":" + review.getAccountId()));
        final FindAndReplaceOptions options = new FindAndReplaceOptions();
        options.upsert();

        return mongoTemplate.findAndReplace(query, review, options);
    }
}
