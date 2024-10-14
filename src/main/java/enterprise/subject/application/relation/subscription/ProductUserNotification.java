package enterprise.subject.application.relation.subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userId;

    @Builder
    public ProductUserNotification(
            Long id,
            Long productId,
            Long userId
    ) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
    }
}
