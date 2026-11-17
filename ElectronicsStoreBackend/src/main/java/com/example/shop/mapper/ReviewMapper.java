package com.example.shop.mapper;

import com.example.shop.dto.response.ReviewResponse;
import com.example.shop.model.entity.Review;
import com.example.shop.model.entity.User;
import com.example.shop.util.JsonConverter;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    private final JsonConverter json;

    public ReviewMapper(JsonConverter json) {
        this.json = json;
    }

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                authorName(review.getUser()),
                review.getRating(),
                review.getTitle(),
                review.getComment(),
                review.getPros(),
                review.getCons(),
                json.toStringList(review.getImages()),
                review.getIsVerifiedPurchase(),
                review.getHelpfulCount(),
                review.getNotHelpfulCount(),
                review.getAdminReply(),
                review.getAdminReplyAt(),
                review.getStatus(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }

    private String authorName(User user) {
        String lastName = user.getLastName();
        String initial = lastName == null || lastName.isEmpty() ? "" : " " + lastName.charAt(0) + ".";
        return user.getFirstName() + initial;
    }
}
