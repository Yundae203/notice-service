package enterprise.subject.application.relation.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {
    List<ProductUserNotification> findByProductId(Long productId);
}
