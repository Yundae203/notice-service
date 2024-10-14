package enterprise.subject.notification.product_user_notification.repository;

import enterprise.subject.notification.product_user_notification.model.ProductUserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {
    List<ProductUserNotification> findByProductIdOrderByUpdateAtAsc(Long productId);

    ProductUserNotification findByProductIdAndUserId(Long productId, Long userId);
}
