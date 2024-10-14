package enterprise.subject.notification.product_user_notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userId;

    private boolean active;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;


    @Builder
    public ProductUserNotification(
            Long id,
            Long productId,
            Long userId,
            boolean active
    ) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.active = active;
    }

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    public void changeActive() {
        this.active = !this.active;
    }
}
