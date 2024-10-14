package enterprise.subject.notification.product_user_notification.repository;

import enterprise.subject.notification.product_user_notification.model.ProductUserNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductUserNotificationHistoryRepository extends JpaRepository<ProductUserNotificationHistory, Integer> {
}
