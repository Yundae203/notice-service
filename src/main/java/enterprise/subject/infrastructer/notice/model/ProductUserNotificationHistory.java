package enterprise.subject.infrastructer.notice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userId;
    private Integer stockVersion;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Builder
    public ProductUserNotificationHistory(
            Long productId,
            Long userId,
            Integer restockVersion,
            NotificationStatus status
    ) {
        this.productId = productId;
        this.userId = userId;
        this.stockVersion = restockVersion;
        this.status = status;
    }

    public void changeStatus(NotificationStatus status) {
        this.status = status;
    }
}
