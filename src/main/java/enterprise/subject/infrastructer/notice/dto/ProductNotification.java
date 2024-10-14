package enterprise.subject.infrastructer.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductNotification {

    private final Long productId;
    private final Integer reStockVersion;
    private final Long userId;
    private boolean isLast = false;

    @Builder
    public ProductNotification(Long productId, Long userId, Integer reStockVersion) {
        this.productId = productId;
        this.reStockVersion = reStockVersion;
        this.userId = userId;
    }

    public void endElement() {
        this.isLast = true;
    }
}
