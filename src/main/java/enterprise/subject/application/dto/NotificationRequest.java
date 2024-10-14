package enterprise.subject.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NotificationRequest(
        @NotNull Long productId,
        @NotNull Integer restockVersion,
        List<Long> users
) {

    public NotificationRequest(Long productId, Integer restockVersion, List<Long> users) {
        this.productId = productId;
        this.restockVersion = restockVersion;
        this.users = users;
    }
}