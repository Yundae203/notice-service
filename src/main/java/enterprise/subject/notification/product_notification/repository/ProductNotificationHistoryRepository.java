package enterprise.subject.notification.product_notification.repository;

import enterprise.subject.notification.product_notification.model.NotificationStatus;
import enterprise.subject.notification.product_notification.model.ProductNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory, Long> {

    List<ProductNotificationHistory> findByProductIdAndStatus(Long productId, NotificationStatus status);
}
