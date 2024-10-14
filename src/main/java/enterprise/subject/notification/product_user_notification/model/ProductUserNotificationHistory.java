package enterprise.subject.notification.product_user_notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userId;
    private Integer restockVersion;

    private LocalDateTime createAt;

    @Builder
    public ProductUserNotificationHistory(
            Long id,
            Long productId,
            Long userId,
            Integer restockVersion
    ) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.restockVersion = restockVersion;
    }

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
    }
}
