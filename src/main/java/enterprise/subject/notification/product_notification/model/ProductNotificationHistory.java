package enterprise.subject.notification.product_notification.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer stockVersion;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Long lastUser;

    @Builder
    public ProductNotificationHistory(
            Long productId,
            Integer restockVersion,
            NotificationStatus status,
            Long lastUser
            ) {
        this.productId = productId;
        this.stockVersion = restockVersion;
        this.status = status;
        this.lastUser = lastUser;
    }

    public void changeStatus(NotificationStatus status) {
        this.status = status;
    }
}